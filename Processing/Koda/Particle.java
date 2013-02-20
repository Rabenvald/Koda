import processing.core.PVector;
import processing.core.PGraphicsJava2D;
import java.util.Random;

public class Particle
{
  private final PVector OrigionalStaticAcceleration = new PVector(0.2f, 0f, 0f);//0.2
  private final PVector OrigionalStaticVelocity = new PVector(1.4f, 0f, 0f); //1.4
  private final float OrigionalDrag = 0.99f;
  
  private ParticleTypes type = ParticleTypes.NULL;
  
  public PVector LastPosition = new PVector(-1000f, -1000f, -1000f);
  public PVector Position = new PVector(LastPosition.x, LastPosition.y, LastPosition.z);
  public PVector Velocity = new PVector(0f,0f,0f);
  public PVector EffectiveVelocity = new PVector(0f,0f,0f);
  public PVector LastVelocity = new PVector(0f,0f,0f);
  public PVector LastAcc = new PVector(0f, 0f, 0f);
  public PVector Acceleration = new PVector(0f,0f,0f);
  public PVector StaticAcceleration = new PVector(OrigionalStaticAcceleration.x, OrigionalStaticAcceleration.y, OrigionalStaticAcceleration.z);
  public PVector StaticVelocity = new PVector(OrigionalStaticVelocity.x, OrigionalStaticVelocity.y, OrigionalStaticVelocity.z);
  
  public float Drag = OrigionalDrag;
  
  private float approachMultiplier = 5f;
  private float repelMultiplier = 5f;
  private float linearRepelMultiplier = 5f;
  private float maxSpeed = 300f;
  private float minSpeed = 10f;
  private float spacialScale = 1f;
  private float timeScale = 999f; //Use absure number here to get erronious particles offscreen quickly... Yes, this is a bug.
  private float scale = 1f;
  private float opacity = 255f;
  
  private static float largeRand = 10f;
  private static float smallRand = 0.25f;
  
  public boolean OffScreen;
  public boolean Active = false;
  
  public float lastCallTime = 0.0f;
  
  public Random randomGen = new Random();
  
  public int id;
  
  //Getters
  public float GetScale()
  {
    return scale;
  }
  
  public ParticleTypes GetType()
  {
    return type;
  }
  
  public PVector GetPosition()
  {
    return Position;
  }

  public PVector GetVelocity()
  {
    return Velocity;
  }

  public PVector GetStaticVelocity()
  {
    return StaticVelocity;
  }

  public PVector GetAcceleration()
  {
    return Acceleration;
  }
  
  public float GetOpacity()
  {
    return opacity;
  }
  
  //Setters spacialScale
  public void SetSpacialScale(float value)
  {
    spacialScale = value;
  }
  public void SetScale(float value)
  {
    scale = value;
  }
  
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
    case ChordProgressionThree: 
      setType(ParticleTypes.ChordProgressionThree);
      break;
    case ChordProgressionFour: 
      setType(ParticleTypes.ChordProgressionFour);
      break;
    case ChordProgressionFive: 
      setType(ParticleTypes.ChordProgressionFive);
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
  
  public void SetRelPosition(PVector npv)
  {
    SetRelPosition(npv.x, npv.y, npv.z);
  }
  
  public void SetRelPosition(float nx, float ny, float nz)
  {
    Position.add(new PVector (nx, ny, nz));
  }
  
  public void SetOpacity(float value)
  {
    if (value < 0) value = 0;
    opacity = value;
  }
  
  /*public PVector setVelocity()
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
    //System.out.println("Particle " + id + " ready.");
  }
  
  public Particle(int myID)
  {
    id = myID;
    //System.out.println("Particle " + id + " ready.");
  }
  
  public void Particle(ParticleTypes myType)
  {
    type = myType;
    //System.out.println("Particle " + id + " ready.");
  }
  
  public void Particle(int myID, ParticleTypes myType)
  {
    id = myID;
    type = myType;
    //System.out.println("Particle " + id + " ready.");
  }
  
  public void Simulate()
  {
    //System.out.println("id: "+id+", "+timeScale+", last comm: "+lastCallTime + ", time: "+Koda.getCurrentTime()+", Active: "+Active);
    float newVelMag = 0;
    
    LastVelocity = new PVector (Velocity.x , Velocity.y , Velocity.z);
    if(Float.isNaN(LastVelocity.x) || Float.isNaN(LastVelocity.y) || Float.isNaN(LastVelocity.z))
    {
      System.out.println("Corrupt LV on particle: " + id );
      LastVelocity = new PVector (randomGen.nextFloat() * largeRand - largeRand / 2, randomGen.nextFloat() * largeRand - largeRand / 2, 0);
    }
    if(Float.isNaN(Velocity.x) || Float.isNaN(Velocity.y) || Float.isNaN(Velocity.z))
    {
      System.out.println("(Stage 1)Corrupt V on particle: " + id );
      Velocity = new PVector (LastVelocity.x + randomGen.nextFloat() * smallRand - smallRand / 2, LastVelocity.y + randomGen.nextFloat() * smallRand - smallRand / 2, 0);
    }
    if(Float.isNaN(Acceleration.x) || Float.isNaN(Acceleration.y) || Float.isNaN(Acceleration.z))
    {
      System.out.println("Corrupt A on particle: " + id + ", " + LastAcc + ", " + Acceleration);
      Acceleration = new PVector (0, 0, 0);
    }
    if(Float.isNaN(LastPosition.x) || Float.isNaN(LastPosition.y) || Float.isNaN(LastPosition.z))
    {
      LastPosition = new PVector(-1000f, -1000f, -1000f);
      System.out.println("Corrupt LP on particle: " + id );
    }
    if(Float.isNaN(Position.x) || Float.isNaN(Position.y) || Float.isNaN(Position.z))
    {
      Position = new PVector(LastPosition.x, LastPosition.y, LastPosition.z);
      System.out.println("Corrupt Pos on particle: " + id );
    }
    
    Velocity = new PVector (LastVelocity.x, LastVelocity.y + randomGen.nextFloat() * largeRand - largeRand / 2, LastVelocity.z);

    Velocity.add(new PVector (Acceleration.x, Acceleration.y, Acceleration.z));
  
    newVelMag = this.Velocity.mag();
    
    if (newVelMag != 0)
    {
      Velocity.setMag(newVelMag * Drag);
    }
    newVelMag = Velocity.mag();
    if (newVelMag > maxSpeed)
    {
      Velocity.setMag(maxSpeed);
    } 
    else if (newVelMag < minSpeed)
    {
      Velocity.setMag(minSpeed);
    } 

    EffectiveVelocity = new PVector (Velocity.x, Velocity.y, Velocity.z);
    EffectiveVelocity.mult(timeScale);

    Position.add( new PVector ((EffectiveVelocity.x + StaticVelocity.x), (EffectiveVelocity.y + StaticVelocity.y), EffectiveVelocity.z + StaticVelocity.z));

    Acceleration = new PVector(StaticAcceleration.x, StaticAcceleration.y, StaticAcceleration.z);
    LastPosition = new PVector (Position.x, Position.y, Position.z);
    LastAcc = new PVector (Acceleration.x , Acceleration.y , Acceleration.z);
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
    if (Float.isNaN(target.Position.x) || target.Position.x == Float.POSITIVE_INFINITY || target.Position.x == Float.NEGATIVE_INFINITY) System.out.println("target == broken " + target);
    Attract(target.Position);
  }

  public void Attract(PVector itarget)
  {
    if (itarget != null && !Float.isNaN(itarget.x))
    {
      PVector target = new PVector (itarget.x, itarget.y, itarget.z);
      if (Float.isNaN(target.x) || target.x == Float.POSITIVE_INFINITY || target.x == Float.NEGATIVE_INFINITY) System.out.println("target pv == broken " + target);
      target.sub(Position);
      float targmag = target.mag();
      if (targmag != 0)
      {
        target.setMag(1/targmag);
        if (Float.isNaN(target.x) || target.x == Float.POSITIVE_INFINITY || target.x == Float.NEGATIVE_INFINITY) System.out.println("target pv == broken now " + target);
        target.mult(approachMultiplier);
        Acceleration.add(target);
        //System.out.println(id + " targ: " + target);
      }
    }
  }

  public void Repel(Particle target)
  {
    Repel(target.Position);
  }
 
  public void Repel(Particle target, float multiplier)
  {
    if (target != null)
    {
      Repel(target.Position, multiplier);
    }
  }

  public void Repel(PVector itarget)
  {
    if (itarget != null)
    {
      PVector pos = new PVector (Position.x, Position.y, Position.z);
      PVector target = new PVector (itarget.x, itarget.y, itarget.z);
      pos.sub(target);
      pos.mult(repelMultiplier);
      float posmag = pos.mag();
        if (posmag != 0)
        {
          pos.setMag(1/posmag); //|| target.x == Float.POSITIVE_INFINITY || target.x == Float.NEGATIVE_INFINITY
          
        }
        //System.out.println("PsvVex: " + pos);
      Acceleration.add(pos);
    }
  }

  public void Repel(PVector itarget, float multiplier)
  {
    if (itarget != null)
    {
      PVector pos = new PVector (Position.x, Position.y, Position.z);
      PVector target = new PVector (itarget.x, itarget.y, itarget.z);
      pos.sub(target);
      pos.mult(multiplier);
      float posmag = pos.mag();
        if (posmag != 0)
        {
          pos.setMag(1/posmag);
        }
      Acceleration.add(pos);
    }
  }

  public void LinearRepel(Particle target)
  {
    if (target != null)
    {
      LinearRepel(target.Position);
    }
  }

  public void LinearRepel(PVector itarget)
  {
    if (itarget != null)
    {
      PVector pos = new PVector (Position.x, Position.y, Position.z);
      PVector target = new PVector (itarget.x, itarget.y, itarget.z);
      pos.sub(target);
      pos.mult(linearRepelMultiplier);
      pos.div(pos.mag());
      Acceleration.add(pos);
    }
  }

  public PVector RepelVector(Particle target)
  {
    return RepelVector(target.Position);
  }

  public PVector RepelVector(PVector itarget)
  {
    if (itarget != null)
    {
      PVector pos = new PVector (Position.x, Position.y, Position.z);
      PVector target = new PVector (itarget.x, itarget.y, itarget.z);
      pos.sub(target);
      pos.mult(repelMultiplier);
      float posmag = pos.mag();
        if (posmag != 0 )//&& !Float.isNaN(posmag)
        {
          pos.setMag(1);
        }
      return pos;
    }
    else
      return new PVector (randomGen.nextFloat() * largeRand - largeRand / 2, randomGen.nextFloat() * largeRand - largeRand / 2, 0);
  }

  public PVector ApproachVector(Particle target)
  {
    return ApproachVector(target.Position);
  }

  public PVector ApproachVector(PVector itarget)
  {
    if (itarget != null)
    {
      PVector target = new PVector (itarget.x, itarget.y, itarget.z);
      target.sub(Position);
      target.mult(repelMultiplier);
      target.div(target.mag());
      return target;
    }
    else
      return new PVector (randomGen.nextFloat() * largeRand - largeRand / 2, randomGen.nextFloat() * largeRand - largeRand / 2, 0);
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

  public void reset()
  {
    LastPosition = new PVector(-1000f,-1000f,-1000f);
    Position = new PVector(-1000f,-1000f,-1000f);
    Velocity = new PVector(0f,0f,0f);
    Acceleration = new PVector(0f,0f,0f);
    LastVelocity = new PVector(0f,0f,0f);
    StaticAcceleration = new PVector(OrigionalStaticAcceleration.x, OrigionalStaticAcceleration.y, OrigionalStaticAcceleration.z);
    StaticVelocity  = new PVector(OrigionalStaticVelocity.x, OrigionalStaticVelocity.y, OrigionalStaticVelocity.z);
    Active = false;
  }
  
  public void init()
  {
    LastPosition = new PVector(-1000f,-1000f,-1000f);
    Position = new PVector(-1000f,-1000f,-1000f);
    Velocity = new PVector(0f,0f,0f);
    Acceleration = new PVector(0f,0f,0f);
    LastVelocity = new PVector(0f,0f,0f);
    StaticAcceleration = new PVector(OrigionalStaticAcceleration.x, OrigionalStaticAcceleration.y, OrigionalStaticAcceleration.z);
    StaticVelocity  = new PVector(OrigionalStaticVelocity.x, OrigionalStaticVelocity.y, OrigionalStaticVelocity.z);
    Drag = OrigionalDrag;
    Active = true;
  }
  
}
