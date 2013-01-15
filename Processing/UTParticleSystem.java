//================================================
// Un-Threaded Particle System
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

public class UTParticleSystem
{
  private ExecutorService executor;
  private Collection<Callable<Integer>> tasks;
  
  
  private final int MAX_NUM_PARTICLES = 512;
  
  public PVector Position;

  private Particle[] particlesIndex;
  private Particle[] threads;
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

  public UTParticleSystem(Koda myOwner)
  {
    particlesIndex = new Particle[MAX_NUM_PARTICLES];
    Owner = myOwner;
    Position = new PVector(Owner.ScreenSize.x / 2, Owner.ScreenSize.y / 2, 0);
  }
  
  public UTParticleSystem(boolean Initialized, Koda myOwner)
  {
    particlesIndex = new Particle[MAX_NUM_PARTICLES];
    Owner = myOwner;
    if(Initialized)
    {
      init();
    }
  }
  
  public UTParticleSystem(boolean Initialized, Koda myOwner, PVector myPosition)
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
      System.out.println(particlesIndex[p].Position);
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
    for (int s = 0; s < AvailableCores; s++) // Create one arraylist for each available core and assign particles to cores
    {
      particleArray.add(new ArrayList<Particle>());
      int splitFrequency = particles.size() / AvailableCores;
      for(int d = splitFrequency * s; d < splitFrequency * (s + 1) && d < particles.size(); d++)//Particle thisParticle : particles)
      {
        particleArray.get(s).add(particles.get(d));
      }
      if (particles.size() == 1)
      {
        particleArray.get(0).add(particles.get(0));
      }
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
      //System.out.println(":o"); 
      myParticle.Simulate();
      if (!myParticle.Active)
      {
        System.out.println("deleted something");
        //System.out.println(particles.get(i).id);
        myParticle.reset();
        //System.out.println(particles.get(i).Velocity + ", " + particles.get(i).Acceleration);
        particles.remove(i);
        i--;
      }
    }
    /*if (particles.size() == 1)
    {
     System.out.println("herro"); 
    }*/
  }
  
  public void SpawnParticle(String type)
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
    
    System.out.println("Using Particle " + myParticleIndex);
    
    particlesIndex[myParticleIndex].init();
    
    switch(ParticleTypes.getType(type.toString())) 
    {
      case ChordProgression: 
        System.out.println("Spawning ChordProgression:");
        particlesIndex[myParticleIndex].Position = new PVector(cSpawnPos.x, cSpawnPos.y, 0);
        break;
      case Melody: 
        System.out.println("Spawning Melody");
        particlesIndex[myParticleIndex].Position = new PVector(mSpawnPos.x, mSpawnPos.y, 0);
        break;
      case Dynamics: 
        System.out.println("Spawning Dynamics"); 
        particlesIndex[myParticleIndex].Position = new PVector(dSpawnPos.x, dSpawnPos.y, 0);
        break;
      case Rhythm: 
        System.out.println("Spawning Rhythm"); 
        particlesIndex[myParticleIndex].Position = new PVector(rSpawnPos.x, rSpawnPos.y, 0);
        break;
      case NULL: 
        break;
      default:
        System.out.println("Unknown particle type: " + type);
        break;
    }
    particlesIndex[myParticleIndex].setType(type);
    particles.add(particlesIndex[myParticleIndex]);
    particlesIndex[myParticleIndex].Impulse(0, randomGen.nextFloat()*100 - 50, 0);
    //cSpawnPos
    
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
  
  private void spawnParticlesFromQueue()
  {
    String element;
    while ((element = particleAddQueue.poll()) != null) 
    {
      SpawnParticle(element);
      SpawnParticle("NULL"); //Stupid bug work-around
    } 
    
  }
  
  public void reset()
  {
    
  }
}

