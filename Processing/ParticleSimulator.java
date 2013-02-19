import processing.core.PVector;
import java.util.concurrent.*;
//import java.util.Random;
import java.lang.Math;

public class ParticleSimulator implements Callable, Runnable
{
  //ArrayList<Particle>
  private ParticleSystem owner;
  private int particleSet;
  private Particle[] particles;
  //public Random randomGen = new Random();
  //private ArrayList<Particle>
  
  public Integer call () throws java.io.IOException 
  {
    simulate();
    return 1;
  }
  
  //@Override
  @Override public void run()
  {
    simulate();
  }
  
  public ParticleSimulator(ParticleSystem myOwner, int mySubset) //temp
  {
    owner = myOwner;
    particleSet = mySubset;
  }
  
  public void simulate()
  {
    PVector effectorPos;
    PVector affectorPos;
    
    particles = owner.particles.toArray(new Particle[owner.particles.size()]);
    for(Particle effectedParticle : owner.particleArray.get(particleSet))//O(n)effiency if all elements are contained within
    {
      effectorPos = effectedParticle.GetPosition();
      effectedParticle.setTimeScale(owner.Owner.deltaTime);
      effectedParticle.lastCallTime = Koda.getCurrentTime();
      //System.out.println(effectedParticle.id + ", dT:"+owner.Owner.deltaTime);
      for(Particle affectorParticle : particles) //O(n^2)effiency
      {
        if (affectorParticle.Active)
        {
          affectorPos = affectorParticle.GetPosition();
          if (effectedParticle != affectorParticle && affectorParticle != null) //Avoid extranious calculations by ignoring itself
          {
            PVector testVector = new PVector(affectorParticle.Position.x, affectorParticle.Position.y, 0);
            float particleDistance = (new PVector(Math.abs(effectorPos.x - affectorPos.x), Math.abs(effectorPos.y - affectorPos.y)).mag());
            if ( particleDistance < 70)
            {
              if ( particleDistance < 20)
              {
                  PVector impVector = effectedParticle.RepelVector(affectorParticle);
                  effectedParticle.Impulse(impVector);
              }
                PVector impVector = effectedParticle.RepelVector(affectorParticle);
                /*if(particleDistance < 1 && effectedParticle.Velocity.mag() < 0.0001)          
                {
                  impVector.add(new PVector(randomGen.nextFloat()*2000 - 1000, randomGen.nextFloat()*2000 - 1000, 0));
                }*/
                //impVector.add(new PVector(randomGen.nextFloat()*2, randomGen.nextFloat()*2, 0));
                //impVector.mult(100f);
                //effectedParticle.Impulse(impVector);
                effectedParticle.Repel(affectorParticle);
            }
            else if ( particleDistance < 300)
            {
             effectedParticle.Attract(affectorParticle); 
            }
            
            //effectedParticle.Attract(affectorParticle); 
          }
          
          PVector distanceTest = effectedParticle.RepelVector(new PVector(effectedParticle.Position.x, owner.Owner.ScreenSize.y/2, 0)); //new PVector (effectedParticle.Position.x, effectedParticle.Position.y, effectedParticle.Position.z);

        }

        if(effectorPos.x > Koda.IntendedScreenSize.x + 50 || effectorPos.y > Koda.IntendedScreenSize.y + 50 || effectorPos.x < -50 || effectorPos.y < -50)
        {
          effectedParticle.OffScreen = true;
          effectedParticle.Active = false;
          effectedParticle.reset();
        }
        
      }

    }

  }
  
}
