import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.*;
import java.util.Collection;
import java.util.Random;
import java.util.ArrayList;
import processing.core.PVector;

public class ParticleSystem
{
  private final int MAX_NUM_PARTICLES = 50;
  private ExecutorService executor;
  
  public PVector Position;
  //public PVector ScreenSize;
  private Collection<Callable<Integer>> tasks;
  private ArrayList<Integer> rawTasks;
  private Particle[] particlesIndex;
  private Particle[] threads;
  private int AvailableCores = 1;
  private boolean maxCoresSet = false;
  
  public ArrayList<Particle> particles;
  public ArrayList<ArrayList<Particle>> particleArray;
  public Koda Owner;
  public Random randomGen = new Random();

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
    AvailableCores = Runtime.getRuntime().availableProcessors();
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
    
    if (!maxCoresSet)
    {
      setAvailableCores(); 
    }
    executor = Executors.newFixedThreadPool(AvailableCores);
    tasks = new ArrayList<Callable<Integer>>(AvailableCores);
    
    for (int p = 0; p < MAX_NUM_PARTICLES; p++)
    {
      particlesIndex[p] = new Particle(p);
      particlesIndex[p].Position = new PVector(randomGen.nextInt(200), randomGen.nextInt(200), randomGen.nextInt(200));
      //particlesIndex[p].id = p;
    }
    
    System.out.println("Available Cores: " + AvailableCores);
    
    test();
    for (int s = 0; s < AvailableCores; s++) // Create one arraylist for each available core and assign particles to cores
    {
      particleArray.add(new ArrayList<Particle>());
    }
  } 
  
  public void test()
  {
    for (Particle next : particlesIndex) 
    {
      particles.add(next);
    }
  }
  
  public void simulate()
  {
    //particles.partition(particleArray, AvailableCores);
    particleArray.clear();
    for (int s = 0; s < AvailableCores; s++) // Create one arraylist for each available core and assign particles to cores
    {
      particleArray.add(new ArrayList<Particle>());
      int splitFrequency = particles.size() / AvailableCores;
      for(int d = splitFrequency * s; d < splitFrequency * (s + 1) && d < particles.size(); d++)//Particle thisParticle : particles)
      {
        //if(particles.size / AvailableCores * (s + 1) //if s = availableCores particles.size
        particleArray.get(s).add(particles.get(d));
      }
    } 
//ParticleSimulator bob = new ParticleSimulator(this, i);
System.out.println("Beginning...");
    
    for (int i = 0; i < particleArray.size(); i++)
    {
      //tasks.add(new ParticleSimulator(this, i));
      
      /*FutureTask ThreadedSimulator = new FutureTask (new ParticleSimulator(this, i));
      System.out.println("Doing " + i);
      executor.submit (task);

      executor.execute(new ParticleSimulator(this, i));*/
      //ParticleSimulator bob = new ParticleSimulator(this, i);
      //bob.simulate();
      System.out.println("Finishing " + i);
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
    //try 
    //{
      /* The tasks are now running concurrently. We wait until all work is done, 
       * with a timeout of 50 seconds: */
      //boolean b = executor.awaitTermination(50, TimeUnit.SECONDS);
      /* If the execution timed out, false is returned: */
      /*System.out.println("All done: " + b);
    } 
    catch (InterruptedException e) 
    { 
      e.printStackTrace(); 
    }*/

    /*if (AvailableCores > 1)
    {
      
    }
    for(Particle effectedParticle : particles)//O(n)effiency
    {
      for(Particle affectorParticle : particles) //O(n^2)effiency
      {
        if (effectedParticle != affectorParticle) //Avoid extranious calculations by ignoring itself
        {
          
        }
      }
    }*/
    
    for(int i = 0; i < particles.size(); i++) //Apply forces after all calculations have been completed
    {
      particles.get(i).Simulate();
      System.out.println("efsdfsd "+ randomGen.nextInt());
    }
  }
  

  
  public void reset()
  {
    
  }
}

