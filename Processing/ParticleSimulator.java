import processing.core.PVector;
import java.util.concurrent.*;
import java.lang.Math;

public class ParticleSimulator implements Callable, Runnable
{
  //ArrayList<Particle>
  private UTParticleSystem owner;
  private int particleSet;
  private Particle[] particles;
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
  
  public ParticleSimulator(UTParticleSystem myOwner, int mySubset) //temp
  {
    owner = myOwner;
    particleSet = mySubset;
  }
  
  public void simulate()
  {
    particles = owner.particles.toArray(new Particle[owner.particles.size()]);
    //System.out.println("Size: " + owner.particles.size() + ", " + owner.particleArray.get(particleSet).size());
    for(Particle effectedParticle : owner.particleArray.get(particleSet))//O(n)effiency if all elements are contained within
    {
       //System.out.println("hi");
      for(Particle affectorParticle : particles) //O(n^2)effiency
      {
        effectedParticle.setTimeScale(owner.Owner.deltaTime);
        if (effectedParticle != affectorParticle) //Avoid extranious calculations by ignoring itself
        {
          
          if ((new PVector(Math.abs(effectedParticle.Position.x - affectorParticle.Position.x), Math.abs(effectedParticle.Position.y - affectorParticle.Position.y)).mag()) < 50)
          {
            PVector impVector = effectedParticle.RepelVector(affectorParticle);
            //impVector.mult(100f);
            //effectedParticle.Impulse(impVector);
          }
          else
          {
           effectedParticle.Attract(affectorParticle); 
          }
        }
        
        /*PVector testing = new PVector(1000, 1000, 1000); // effectedParticle.Position
        testing.sub(owner.Position);
        if (testing.mag() > owner.Position.mag() )
        {
            effectedParticle.Approach(owner.Position);
        }*/
      }
      //System.out.println(effectedParticle.id);
      //effectedParticle.Attract(new PVector (300,200,0));System.out.println("ha"+effectedParticle.Acceleration);
      if(effectedParticle.Position.x > owner.Owner.ScreenSize.x + 50 || effectedParticle.Position.y > owner.Owner.ScreenSize.y + 50 || effectedParticle.Position.x < -50 || effectedParticle.Position.y < -50 || Float.isNaN(effectedParticle.Position.x) )
      {
        
        effectedParticle.OffScreen = true;
        effectedParticle.Active = false;
        effectedParticle.reset();
      }
    }
    /*if (particleSet == 0 && owner.particles.size() == 1)
    {
      Particle effectedParticle = owner.particles.get(0);
       if(effectedParticle.Position.x > owner.Owner.ScreenSize.x || effectedParticle.Position.y > owner.Owner.ScreenSize.y || effectedParticle.Position.x < 0 || effectedParticle.Position.y < 0 || Float.isNaN(effectedParticle.Position.x) )
      {
          
          effectedParticle.OffScreen = true;
          effectedParticle.Active = false;
          effectedParticle.reset();
      } 
    }*/
    
    //System.out.println("On core: " + particleSet);
  }
  
}
