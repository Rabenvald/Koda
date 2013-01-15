import processing.core.PVector;
import processing.core.PGraphicsJava2D;

public class Particle
{
  private final PVector OrigionalStaticAcceleration = new PVector(0.4f, 0f, 0f);
  private final float OrigionalDrag = 0.1f;
  
  public PVector Position = new PVector(-1000f,-1000f,-1000f);
  public PVector Velocity = new PVector(0f,0f,0f);
  public PVector LastVelocity = new PVector(0f,0f,0f);
  public PVector Acceleration = new PVector(0f,0f,0f);
  public PVector StaticAcceleration = new PVector(OrigionalStaticAcceleration.x, OrigionalStaticAcceleration.y, OrigionalStaticAcceleration.z);
  
  public float Drag = OrigionalDrag;
  
  private float approachMultiplier = 5f;
  private float repelMultiplier = 5f;
  private float maxSpeed = 1000f;
  private float timeScale = 1f;
  
  public boolean OffScreen;
  public boolean Active = false;
  
  private ParticleTypes type = ParticleTypes.NULL;
  
  public int id;
  
  //Getters
  public ParticleTypes GetType()
  {
    return type;
  }
  
  /*
  public PVector getPosition()
  {
    return Position;
  }
  
  public PVector getVelocity()
  {
    return Velocity;
  }
  
  public PVector getAcceleration()
  {
    return Acceleration;
  }
  */
  //Setters
  public void setTimeScale(float value)
  {
    timeScale = value;
  }
  
  public void setType(String newType)
  {
    switch(ParticleTypes.getType(newType)) 
    {
    case ChordProgression: 
      setType(ParticleTypes.ChordProgression);
      break;
    case Melody: 
      setType(ParticleTypes.Melody);
      break;
    case Dynamics: 
      setType(ParticleTypes.Dynamics);
      break;
    case Rhythm: 
      setType(ParticleTypes.Rhythm); 
      break;
    case NULL: 
      break;
    default:
      System.out.println("Unknown particle type: " + type);
      setType(ParticleTypes.NULL);
      break;
    }
  }
  
  public void setType(ParticleTypes newType)
  {
    type = newType;
  }
  /*
  public PVector setPosition()
  {
    return Position;
  }
  
  public PVector setVelocity()
  {
    return Velocity;
  }
  
  public PVector setAcceleration()
  {
    return Acceleration;
  }
  */
  /*public void Particle(graphic)
  {
    
  }*/
  
  public Particle()
  {
    System.out.println("Particle " + id + " ready.");
  }
  
  public Particle(int myID)
  {
    id = myID;
    System.out.println("Particle " + id + " ready.");
  }
  
  public void Particle(ParticleTypes myType)
  {
    type = myType;
    System.out.println("Particle " + id + " ready.");
  }
  
  public void Particle(int myID, ParticleTypes myType)
  {
    id = myID;
    type = myType;
    System.out.println("Particle " + id + " ready.");
  }
  
  public void Simulate()
  {
    float newVelMag = 0;
     //System.out.println(id + "pre Acc = " + Acceleration + ", Vel = " + Velocity + ", " + Active);
    Velocity = new PVector (LastVelocity.x, LastVelocity.y, LastVelocity.z);
    //System.out.println( "Pre Acc = " + Acceleration + ", Vel = " + Velocity);
    Velocity.add(new PVector (Acceleration.x, Acceleration.y, Acceleration.z));
     
    newVelMag = Velocity.mag() - Drag;
     
    if (newVelMag < 0)
      Velocity.setMag(0);
    else
     Velocity.setMag(Velocity.mag() - Drag);
      
      if (id == 0)
    //System.out.println(id + " Acc = " + Acceleration + ", Vel = " + Velocity + ", " + Active);
    
    if (Velocity.mag() > maxSpeed)
      Velocity.setMag(maxSpeed);
      
    LastVelocity = new PVector (Velocity.x, Velocity.y, Velocity.z);
    Velocity.mult(timeScale);
    Position.add( new PVector (Velocity.x, Velocity.y, Velocity.z));
    //System.out.println( "Particle " + id + " is at " + Position);
    //System.out.println( "Acc = " + Acceleration + ", Vel = " + Velocity);
    //System.out.println( "Time Scale = " + timeScale);
    //display();
    //Velocity = new PVector(0f, 0f, 0f);
    Acceleration = new PVector(StaticAcceleration.x, StaticAcceleration.y, StaticAcceleration.z);
    //System.out.println(id + "post Acc = " + Acceleration + ", Vel = " + Velocity + ", " + Active);
  }
  
  public void Approach(Particle target)
  {
    Approach(target.Position);
  }
  
  public void Approach(PVector target)
  {
    target.sub(Position);
    target.mult(approachMultiplier);
    Acceleration.add(target);
  }
  
  public void Attract(Particle target)
  {
    Attract(target.Position);
  }
  
  public void Attract(PVector itarget)
  {
    //System.out.println( "Acc = " + Acceleration + ", Vel = " + Velocity);target
    //System.out.println( "Targ = " + target);
    PVector target = new PVector (itarget.x, itarget.y, itarget.z);
    target.sub(Position);
    //System.out.println( "Targ op1 = " + target);
    target.setMag(1/target.mag());
    //System.out.println( "Targ op2 = " + target);
    target.mult(approachMultiplier);
    
    //System.out.println( "Targ op3 = " + target);
    Acceleration.add(target);
  }
  
  public void Repel(Particle target)
  {
    Repel(target.Position);
  }
  
  public void Repel(PVector itarget)
  {
    PVector target = new PVector (itarget.x, itarget.y, itarget.z);
    target.add(Position);
    target.mult(repelMultiplier);
    target.div(target.mag());
    Acceleration.add(target);
  }
  
  public PVector RepelVector(Particle target)
  {
    return RepelVector(target.Position);
  }
  
  public PVector RepelVector(PVector itarget)
  {
    PVector target = new PVector (itarget.x, itarget.y, itarget.z);
    target.add(Position);
    target.mult(repelMultiplier);
    target.div(target.mag());
    return target;
  }
  
  public void AddForce(PVector newForce)
  {
   StaticAcceleration.add(newForce);
  }
  
  public void AddForce(float nx, float ny, float nz)
  {
   AddForce(new PVector(nx, ny, nz));
  }
  
  public void Impulse(PVector newForce)
  {
   Acceleration.add(newForce);
  }
  
  public void Impulse(float nx, float ny, float nz)
  {
   Impulse(new PVector(nx, ny, nz));
  }
  
  public void Impulse(float nx, float ny)
  {
   Impulse(new PVector(nx, ny, 0));
  }
  
  public void LoopAcross()
  {
    //Position = new PVector(0f, 0f, 0f);
  }
  
  public void reset()
  {
    Position = new PVector(-1000f,-1000f,-1000f);
    Velocity = new PVector(0f,0f,0f);
    Acceleration = new PVector(0f,0f,0f);
    LastVelocity = new PVector(0f,0f,0f);
    StaticAcceleration = new PVector(OrigionalStaticAcceleration.x, OrigionalStaticAcceleration.y, OrigionalStaticAcceleration.z);
    Active = false;
  }
  
  public void init()
  {
    Position = new PVector(-1000f,-1000f,-1000f);
    Velocity = new PVector(0f,0f,0f);
    Acceleration = new PVector(0f,0f,0f);
    LastVelocity = new PVector(0f,0f,0f);
    StaticAcceleration = new PVector(OrigionalStaticAcceleration.x, OrigionalStaticAcceleration.y, OrigionalStaticAcceleration.z);
    Active = true;
  }
  
  void display()
  {
    
    /*switch(ParticleTypes.getType(type.toString())) 
    {
    case ChordProgression: 
      System.out.println("Testing:");
      break;
    case Melody: 
      System.out.println("Melody recieved");
      break;
    case Dynamics: 
      System.out.println("Dynamics revieved"); 
      break;
    case Rhythm: 
      System.out.println("Rhythm revieved"); 
      break;
    case NULL: 
      int r = 10; //temp
      PGraphicsJava2D.stroke(0);
      fill(0,100);
      ellipse(pos.x,pos.y ,r,r);
      break;
    default:
      System.out.println("Unknown particle type: " + type);
      break;
    }*/
    //Position.mult(3.1f);
    /*float r = 1f;
    stroke(0);
    fill(0,100);
    ellipse(x,y,r,r);*/
  }
  
}
