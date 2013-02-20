//================================================
// Threaded Particle System
//================================================
/*import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;*/
import java.util.concurrent.*;
import java.util.Collection;
import java.util.Queue;
import java.util.Random;
import java.util.ArrayList;
import processing.core.PVector;

public class ParticleSystem
{
  private ExecutorService executor;
  private Collection<Callable<Integer>> tasks;
  
  
  private final int MAX_NUM_PARTICLES = 512;
  
  public PVector Position;

  private Particle[] particlesIndex;
  private Particle[] threads;
  private float launchVelMult = 100f;
  private int AvailableCores = 1;
  private boolean maxCoresSet = false;
  
  private Queue<String> particleAddQueue;
  
  public ArrayList<Particle> particles;
  public ArrayList<ArrayList<Particle>> particleArray;
  public Koda Owner;
  public Random randomGen = new Random();
  
  public PVector cSpawnPos = new PVector (200, 200);
  public PVector rSpawnPos = new PVector (300, 200);
  public PVector mSpawnPos = new PVector (400, 200);
  public PVector dSpawnPos = new PVector (500, 200);
  
  public int DEBUG_numParticles;

  public ParticleSystem(Koda myOwner)
  {
    particlesIndex = new Particle[MAX_NUM_PARTICLES];
    Owner = myOwner;
    Position = new PVector(Owner.ScreenSize.x / 2, Owner.ScreenSize.y / 2, 0);
  }
  
  public ParticleSystem(boolean Initialized, Koda myOwner)
  {
    particlesIndex = new Particle[MAX_NUM_PARTICLES];
    Owner = myOwner;
    if(Initialized)
    {
      init();
    }
  }
  
  public ParticleSystem(boolean Initialized, Koda myOwner, PVector myPosition)
  {
    particlesIndex = new Particle[MAX_NUM_PARTICLES];
    Owner = myOwner;
    if(Initialized)
    {
      init();
    }
    Position = myPosition;
  }
  
  public boolean setAvailableCores()
  {
    AvailableCores = Runtime.getRuntime().availableProcessors();//1;
    maxCoresSet = true;
    return true;
  }
  
  public boolean setAvailableCores(int numCores)
  {
    if(numCores < 1 || numCores > Runtime.getRuntime().availableProcessors())
      return false;
    else
    {
      AvailableCores = numCores;
      maxCoresSet = true;
      return true;
    }
  }
  
  public void init()
  {
    particles = new ArrayList<Particle>();
    particleArray = new ArrayList<ArrayList<Particle>>();
    particleAddQueue = new ConcurrentLinkedQueue();
    
    if (!maxCoresSet)
    {
      setAvailableCores(); 
    }

    executor = Executors.newFixedThreadPool(AvailableCores);
    tasks = new ArrayList<Callable<Integer>>(AvailableCores);
    
    for (int p = 0; p < MAX_NUM_PARTICLES; p++)
    {
      particlesIndex[p] = new Particle(p);
      //particlesIndex[p].init();
      //particlesIndex[p].Position = new PVector(randomGen.nextInt(300)+150, randomGen.nextInt(200)+100, 0);
      //particlesIndex[p].id = p;
      //System.out.println(particlesIndex[p].Position);
    }
    
    System.out.println("Available Cores: " + AvailableCores);
    
    //test();
    for (int s = 0; s < AvailableCores; s++) // Create one arraylist for each available core and assign particles to cores
    {
      particleArray.add(new ArrayList<Particle>());
    }
  } 
  
  public void test()
  { 
    int numTestParticles = 20;
    if (numTestParticles > particlesIndex.length)
      numTestParticles = particlesIndex.length;
      
    for (int i = 0; i < numTestParticles; i++) 
    {
      particles.add(particlesIndex[i]);
      particlesIndex[i].init();
      particlesIndex[i].Position = new PVector(randomGen.nextInt(300)+150, randomGen.nextInt(200)+100, 0);
    }
  }
  
  public void simulate()
  {
    particleArray.clear();
    spawnParticlesFromQueue();
    /*while (particles.size() < AvailableCores)
    {
      SpawnParticle("NULL"); //Stupid bug work-around
    }*/
    for (int s = 0; s < AvailableCores; s++) // Create one arraylist for each available core and assign particles to cores
    {
      particleArray.add(new ArrayList<Particle>());
      int splitFrequency = particles.size() / AvailableCores;
      for(int d = splitFrequency * s; d < splitFrequency * (s + 1) && d < particles.size(); d++)//Particle thisParticle : particles)
      {
        particleArray.get(s).add(particles.get(d));
      }
      /*if (particles.size() == 1)
      {
        particleArray.get(0).add(particles.get(0));
      }*/
    } 
    
    for (int i = 0; i < AvailableCores; i++)//particleArray.size()
    {
      tasks.add(new ParticleSimulator(this, i));
      
      FutureTask ThreadedSimulator = new FutureTask (new ParticleSimulator(this, i));
      //System.out.println("Doing " + i);
      for(Callable e : tasks)
      {
        executor.submit (e);
      }

      executor.execute(new ParticleSimulator(this, i));
    }
    
    
    // This will make the executor accept no new threads
    // and finish all existing threads in the queue
    try 
    {
      executor.invokeAll(tasks);
    } 
    catch(InterruptedException ie) 
    {
      // Handle this
    }
    tasks.clear();
    //executor.shutdown();
    
    for(int i = 0; i < particles.size(); i++) //Apply forces after all calculations have been completed
    {
      Particle myParticle = particles.get(i);
      if (myParticle.GetType().toString().equals("NULL"))
      {
        myParticle.Position = new PVector (-100, -100, -100);
      }
      myParticle.Simulate();
      if (!myParticle.Active)
      {
        //System.out.println("deleted something");
        //System.out.println(particles.get(i).id);
        myParticle.reset();
        particles.remove(i);
        i--;
      }
    }
    if (DEBUG_numParticles != particles.size())
    {
      DEBUG_numParticles = particles.size();
      System.out.println("Num Active Particles: "+DEBUG_numParticles);
    }
    
    //if (particles.size()>0)    System.out.println("ArrList pos 0: "+particles.get(0).id);
  }
  public void SpawnParticle(String type)
  {
    SpawnParticle(type, 0, 0);
  }
  
  public void SpawnParticle(String type, float valueOne, float valueTwo)
  {
    int myParticleIndex = -1;
    
    for (int i = 0; i < particlesIndex.length && (myParticleIndex < 0); i++) 
    {
      //System.out.println("i = " + i + " Status = " + particlesIndex[i].Active);
      if (!particlesIndex[i].Active)
      {
        myParticleIndex = i;
      }
    }
    if (myParticleIndex < 0)
    {
      myParticleIndex = 0;
      System.out.println("All Particles in use...");
    }
    
    //System.out.println("Using Particle " + myParticleIndex);
    
    particlesIndex[myParticleIndex].init();
    
    //int spawnIndex = 0;
    PVector spawnPos = new PVector (-1000, -1000, -1000);
    PVector launchVector = new PVector (0, 0, 0);
    //particlesIndex[myParticleIndex].SetSpacialScale(Owner.getScaleDiscrepancy());
    switch(ParticleTypes.getType(type.toString())) 
    {
      case ChordProgressionThree: 
        System.out.println("Spawning ChordProgression3:"); 
        launchVector = new PVector (Owner.chordSpawnVectors[Owner.setNextChordSpawnPosition((int)valueOne - 1)].x, Owner.chordSpawnVectors[Owner.setNextChordSpawnPosition((int)valueOne - 1)].y, Owner.chordSpawnVectors[Owner.setNextChordSpawnPosition((int)valueOne - 1)].z);
        spawnPos = Owner.getChordSpawnPosition();
        break;
      case ChordProgressionFour: 
        System.out.println("Spawning ChordProgression4:");
        launchVector = new PVector (Owner.chordSpawnVectors[Owner.setNextChordSpawnPosition((int)valueOne - 1)].x, Owner.chordSpawnVectors[Owner.setNextChordSpawnPosition((int)valueOne - 1)].y, Owner.chordSpawnVectors[Owner.setNextChordSpawnPosition((int)valueOne - 1)].z);
        spawnPos = Owner.getChordSpawnPosition();
        break;
      case ChordProgressionFive: 
        System.out.println("Spawning ChordProgression5:");
        launchVector = new PVector (Owner.chordSpawnVectors[Owner.setNextChordSpawnPosition((int)valueOne - 1)].x, Owner.chordSpawnVectors[Owner.setNextChordSpawnPosition((int)valueOne - 1)].y, Owner.chordSpawnVectors[Owner.setNextChordSpawnPosition((int)valueOne - 1)].z);
        spawnPos = Owner.getChordSpawnPosition();
        break;
      case ChordProgression: 
        System.out.println("Spawning ChordProgression:");
        break;
      case Melody: 
        System.out.println("Spawning Melody");
        launchVector = new PVector (Owner.melodySpawnVectors[Owner.genNextMelodySpawnPosition()].x, Owner.melodySpawnVectors[Owner.genNextMelodySpawnPosition()].y, Owner.melodySpawnVectors[Owner.genNextMelodySpawnPosition()].z);
        spawnPos = Owner.getMelodySpawnPosition();
        particlesIndex[myParticleIndex].SetScale(valueOne);
        break;
      case Dynamics: 
        System.out.println("Spawning Dynamics"); 
        launchVector = new PVector (Owner.dynamicsSpawnVectors[Owner.genNextDynamicsSpawnPosition()].x, Owner.dynamicsSpawnVectors[Owner.genNextDynamicsSpawnPosition()].y, Owner.dynamicsSpawnVectors[Owner.genNextDynamicsSpawnPosition()].z);
        spawnPos = Owner.getDynamicsSpawnPosition();
        break;
      case Rhythm: 
        System.out.println("Spawning Rhythm"); 
        launchVector = new PVector (Owner.rhythmSpawnVectors[Owner.genNextRhythmSpawnPosition()].x, Owner.rhythmSpawnVectors[Owner.genNextRhythmSpawnPosition()].y, Owner.rhythmSpawnVectors[Owner.genNextRhythmSpawnPosition()].z);
        spawnPos = Owner.getRhythmSpawnPosition();
        break;
      case NULL: 
        break;
      default:
        System.out.println("Unknown particle type: " + type);
        break;
    }
    System.out.println("Spawning with oVec: " + launchVector);
    particlesIndex[myParticleIndex].Position = new PVector(spawnPos.x, spawnPos.y, spawnPos.z);
    particlesIndex[myParticleIndex].setType(type);
    particles.add(particlesIndex[myParticleIndex]);
    launchVector.mult(launchVelMult);
    particlesIndex[myParticleIndex].Impulse(launchVector);

    //System.out.println(particlesIndex[myParticleIndex].Velocity + ", " + particlesIndex[myParticleIndex].Acceleration);
  }
  
  public void SpawnParticle()
  {
    SpawnParticle("NULL");
  }
  
  public void AddParticle(String type)
  {
    particleAddQueue.add(type);
  }
  
  public void AddParticle(String type, float valueOne)
  {
    String input = type + "," + valueOne;
    particleAddQueue.add(input);
  }
  
  public void AddParticle(String type, float valueOne, float valueTwo)
  {
    String input = type + "," + valueOne + "," + valueTwo;
    particleAddQueue.add(input);
  }
  
  private void spawnParticlesFromQueue()
  {
    String[] parsedString;
    String unParsdSring;
    while ((unParsdSring = particleAddQueue.poll()) != null) 
    {
      parsedString = unParsdSring.split(",");
      if (parsedString.length == 3)
      {
        SpawnParticle(parsedString[0], Float.parseFloat(parsedString[1]), Float.parseFloat(parsedString[2]));
      }
      else if (parsedString.length == 2)
      {
        SpawnParticle(parsedString[0], Float.parseFloat(parsedString[1]), 0);
      }
      else
      {
        SpawnParticle(parsedString[0]);
      }
      
      //SpawnParticle("NULL"); //Stupid bug work-around
    } 
    //SpawnParticle("NULL"); //This makes absolutely no sense.... but it works :/
    //SpawnParticle("NULL");
    //SpawnParticle("NULL");
    //SpawnParticle("NULL");
  }
  
  public void reset()
  {
    
  }
}

