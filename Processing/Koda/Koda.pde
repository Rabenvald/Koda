//=====================================
// Koda
// Authors: Benjamin B. Boozer,IV
// Created: 2012 December 4th
//=====================================
import oscP5.*;
import netP5.*;

OscP5 oscP5;
NetAddress myRemoteLocation;
NetAddress oscRemoteLocation;

static float timerStartTime, currentTime, lastTime, deltaTime, timer = 0; 
final int LOCAL_PORT = 12000;
public final static PVector IntendedScreenSize = new PVector (2048, 872);
public static PVector ScreenSize = new PVector (2048, 872);
private boolean melDown = false;

private PShape chordThreeSprite;
private PShape chordFourSprite;
private PShape chordFiveSprite;
private PShape melodySprite;
private PShape rhythmSprite;

private PShape chordNodeSprite;
private PShape dynamicsNodeSprite;
private PShape melodyNodeSprite;
private PShape rhythmNodeSprite;

private PVector chordNodePosition;
private PVector dynamicsNodePosition;
private PVector melodyNodePosition;
private PVector rhythmNodePosition;

private PVector chordNodeScale;
private PVector rhythmNodeScale;
private PVector melodyNodeScale;
private PVector dynamicsNodeScale;

private PVector[] chordSpawnPositions = {new PVector(246, 330, 0), new PVector(449, 330, 0), new PVector(482, 376, 0), new PVector(451, 562, 0), new PVector(369, 494, 0), new PVector(245, 464, 0), new PVector(225, 442, 0)};
private PVector[] rhythmSpawnPositions = {new PVector(932, 338, 0), new PVector(996, 360, 0), new PVector(932, 458, 0)};
private PVector[] melodySpawnPositions = {new PVector(1148, 530, 0), new PVector(1382, 424, 0)};
private PVector[] dynamicsSpawnPositions = {new PVector(1806, 388, 0), new PVector(1808, 546, 0)};

public final PVector[] chordSpawnVectors = {new PVector(-0.76675767, -0.64193666, 0), new PVector(0.91615736, -0.40081885, 0), new PVector(0.5082536, -0.8612074, 0), new PVector(0.9244386, 0.3813309, 0), new PVector(-0.2046649, 0.9788321, 0), new PVector(-0.8131087, 0.5821119, 0), new PVector(0.20344646, 0.9790861, 0)};
public final PVector[] rhythmSpawnVectors = {new PVector(-0.8155071, -0.578747, 0), new PVector(0.9570244, -0.29000738, 0), new PVector(-0.84117854, 0.5407576, 0)};
public final PVector[] melodySpawnVectors = {new PVector(-0.7151872, 0.69893295, 0), new PVector(-0.79128504, -0.6114475, 0)};
public final PVector[] dynamicsSpawnVectors = {new PVector(0, -1, 0), new PVector(0, 1, 0)};

private float scaleDiscrepancy;

public ParticleSystem myParticleSystem;
public int AvailableCores = Runtime.getRuntime().availableProcessors();

public int lastChordSpawnPosition = 0;
public int lastRhythmSpawnPosition = 0;
public int lastMelodySpawnPosition = 0;
public int lastDynamicsSpawnPosition = 0;

public PVector DEBUG_FirstPos = new PVector (0, 0, 0);
public PVector DEBUG_SecondPos = new PVector (0, 0, 0);
public PVector DEBUG_Vector = new PVector (0, 0, 0);
public boolean mouseDown = false;

public float getScaleDiscrepancy()
{
  return scaleDiscrepancy;
}

public int setNextChordSpawnPosition(int input)
{
  if (input > chordSpawnPositions.length)
  {
    input = chordSpawnPositions.length;
  }
  else if (input < 0)
  {
    input = 0;
  }
  
  lastChordSpawnPosition = input;
  
  return lastChordSpawnPosition;
}
public int genNextChordSpawnPosition()
{
  lastChordSpawnPosition = (int)random(chordSpawnPositions.length);
  return lastChordSpawnPosition;
}
public PVector getChordSpawnPosition()
{
  return new PVector(chordSpawnPositions[lastChordSpawnPosition].x, chordSpawnPositions[lastChordSpawnPosition].y, chordSpawnPositions[lastChordSpawnPosition].z);
}

public int genNextRhythmSpawnPosition()
{
  lastRhythmSpawnPosition = (int)random(rhythmSpawnPositions.length);
  return lastRhythmSpawnPosition;
}
public PVector getRhythmSpawnPosition()
{
  return new PVector(rhythmSpawnPositions[lastRhythmSpawnPosition].x, rhythmSpawnPositions[lastRhythmSpawnPosition].y, rhythmSpawnPositions[lastRhythmSpawnPosition].z);
}

public int genNextMelodySpawnPosition()
{
  lastMelodySpawnPosition = (int)random(melodySpawnPositions.length);
  return lastMelodySpawnPosition; 
}
public PVector getMelodySpawnPosition()
{
  return new PVector(melodySpawnPositions[lastMelodySpawnPosition].x, melodySpawnPositions[lastMelodySpawnPosition].y, melodySpawnPositions[lastMelodySpawnPosition].z);
}

public int genNextDynamicsSpawnPosition()
{
  lastDynamicsSpawnPosition = (int)random(dynamicsSpawnPositions.length);
  return lastDynamicsSpawnPosition;
}

public PVector getDynamicsSpawnPosition()
{
  return new PVector(dynamicsSpawnPositions[lastDynamicsSpawnPosition].x, dynamicsSpawnPositions[lastDynamicsSpawnPosition].y, dynamicsSpawnPositions[lastDynamicsSpawnPosition].z);
}

void setup()
{
  scaleDiscrepancy = /*displayWidth*/1024 / IntendedScreenSize.x;
  ScreenSize.mult(scaleDiscrepancy);
  size((int)ScreenSize.x, (int)ScreenSize.y);
  frameRate(60);
  oscP5 = new OscP5(this, LOCAL_PORT);
  myRemoteLocation = new NetAddress("127.0.0.1", LOCAL_PORT);
  
  loadAssets();
  
  myParticleSystem = new ParticleSystem(true, this, ScreenSize); //fixhere
  
  chordNodePosition = new PVector (0.125*ScreenSize.x/9, ScreenSize.y/2, 0 );
  rhythmNodePosition = new PVector (2.375*ScreenSize.x/9 , ScreenSize.y/2, 0 );
  melodyNodePosition = new PVector (4.625*ScreenSize.x/9 , ScreenSize.y/2, 0 );
  dynamicsNodePosition = new PVector (6.775*ScreenSize.x/9 , ScreenSize.y/2, 0 );
  
  scaleDiscrepancy = ScreenSize.x / IntendedScreenSize.x;
  //PVector[] chordSpawnPositions = {new PVector(chordNodePosition.x + 214.5, 40.959 + chordNodePosition.y / 2), new PVector( chordNodePosition.x + 426.348, 45.189 + chordNodePosition.y / 2), new PVector(chordNodePosition.x + 459.571, 86.082 + chordNodePosition.y / 2), new PVector(chordNodePosition.x + 426.335, 172.667 + chordNodePosition.y / 2), new PVector(chordNodePosition.x + 165.431, 156 + chordNodePosition.y / 2), new PVector(chordNodePosition.x + 213.999, 177 + chordNodePosition.y / 2), new PVector(chordNodePosition.x + 339.676, 208.12 + chordNodePosition.y / 2)}; //println("gsdfgsdfgsdfgSDFGSDFGSDFGsdfgsdfGsdfgsdfgsdfgsf: " + chordSpawnPositions.length);
  /*for(PVector  c : chordSpawnPositions)
  {
    c.mult(scaleDiscrepancy);
  }
  for(PVector  d : dynamicsSpawnPositions)
  {
    d.mult(scaleDiscrepancy);
  }
  for(PVector  m : melodySpawnPositions)
  {
    m.mult(scaleDiscrepancy);
  }
  for(PVector  r : rhythmSpawnPositions)
  {
    r.mult(scaleDiscrepancy);
  }*/
  
  /*chordNodeScale = ScreenSize.x / IntendedScreenSize.x
  dynamicsNodeScale
  melodyNodeScale
  rhythmNodeScale*/
}

void loadAssets()
{
 try
  {
     //assetPath = "..\..\..\SnapdragonDeck\Design\6_Screen Design\8_dev assets\nodes"
     chordThreeSprite = loadShape ("/particles/chordthree.svg");
     chordFourSprite = loadShape ("/particles/chordfour.svg");
     chordFiveSprite = loadShape ("/particles/chordfive.svg");
     melodySprite = loadShape ("/particles/melody.svg");
     rhythmSprite = loadShape ("/particles/rhythm.svg");
     
     chordNodeSprite = loadShape ("/nodes/chordnode.svg");
     dynamicsNodeSprite = loadShape ("/nodes/dynamicsnode.svg");
     melodyNodeSprite = loadShape ("/nodes/melodynode.svg");
     rhythmNodeSprite = loadShape ("/nodes/rhythmnode.svg");
     
     chordThreeSprite.scale(scaleDiscrepancy);
     chordFourSprite.scale(scaleDiscrepancy);
     chordFiveSprite.scale(scaleDiscrepancy);
     melodySprite.scale(scaleDiscrepancy);
     rhythmSprite.scale(scaleDiscrepancy);
     
     chordNodeSprite.scale(scaleDiscrepancy);
     dynamicsNodeSprite.scale(scaleDiscrepancy);
     melodyNodeSprite.scale(scaleDiscrepancy);
     rhythmNodeSprite.scale(scaleDiscrepancy);
  }
  finally
  {
   
  } 
}

public static float getDeltaTime()
{
  return deltaTime;
}

public static float getCurrentTime()
{
  return currentTime / 1000;
}

void draw()
{
  background(255);
  currentTime = millis();
  deltaTime = (currentTime - lastTime)/1000;
  if (deltaTime > 0.1f)
    deltaTime = 0.1f;
  //println ("dT: " + deltaTime);
  //println("x:"+floor(mouseX)/scaleDiscrepancy+" y:"+floor(mouseY)/scaleDiscrepancy);
  myParticleSystem.simulate();
  drawParticles();
  drawNodes();
  lastTime = currentTime;
  if (mouseDown)
    line(DEBUG_FirstPos.x, DEBUG_FirstPos.y, floor(mouseX), floor(mouseY));
    
    
}
void drawDebugShape()
{
  pushMatrix();
  float angle;
  float oldWidth = chordThreeSprite.width;
  float oldHeight = chordThreeSprite.height;
  translate ( scaleDiscrepancy * 100 , scaleDiscrepancy * 100);
  angle = atan2( mouseY , mouseX);
  rotate(angle - degrees(90.005f));
  shape(chordThreeSprite, -chordThreeSprite.width * (scaleDiscrepancy / 2), -chordThreeSprite.height * (scaleDiscrepancy / 2) );
  popMatrix(); 
}
void mousePressed()
{
  mouseDown = true;
  println("x, y: "+(int)floor(mouseX)/scaleDiscrepancy+", "+(int)floor(mouseY)/scaleDiscrepancy + " ");
  DEBUG_FirstPos = new PVector (floor(mouseX), floor(mouseY));
}
void mouseReleased()
{
  mouseDown = false;
  DEBUG_SecondPos = new PVector (floor(mouseX), floor(mouseY));
  DEBUG_Vector = new PVector (DEBUG_SecondPos.x, DEBUG_SecondPos.y);
  DEBUG_Vector.sub(DEBUG_FirstPos);
  DEBUG_Vector.setMag(1);
  println(DEBUG_Vector);
}
void keyReleased()
{
  if (key == CODED) 
  {
    if (keyCode == RIGHT) 
    {
      //System.out.println("Release/////");
      sendOSC("/mel",(int)0,(int)random(5)+1,(int)random(255));
      melDown = false;
      //System.out.println("/////Release");
    } 
  }
}

void keyPressed() 
{
  if (key == CODED) 
  {
    if (keyCode == UP) 
    {
      sendOSC("/chord",(int)random(7) + 1,(int)random(3)); 
    } 
    else if (keyCode == DOWN) 
    {
      sendOSC("/dyn",(int)random(128));
    } 
    else if (keyCode == RIGHT) 
    {
      if (!melDown)
      {
        sendOSC("/mel",1,(int)random(5) + 1);
      }
      melDown = true;
    } 
    else if (keyCode == LEFT) 
    {
      sendOSC("/rhy",(int)random(3) + 1);  
    } 
    else if (keyCode == CONTROL) 
    {
      sendOSC("/TIME",(int)currentTime);
    } 
  } 
}

void sendOSC(String addrPattern, int command)
{
  sendOSC(addrPattern, command, 0, 0);
}

void sendOSC(String addrPattern, int command, int value)
{
  sendOSC(addrPattern, command, value, 0);
}

void sendOSC(String addrPattern, int channel, int command, int value)
{
  OscMessage myMessage = new OscMessage(addrPattern);
  myMessage.add(channel);          //identifier for routing in PD
  myMessage.add(command);         // add an int to the osc message
  myMessage.add(value); 
  //send the message
  oscP5.send(myMessage, myRemoteLocation);
  println("Sent: " + addrPattern + ", " + channel + "," + command + "," + value);
}

void sendOSC(int channel, int command, int value)
{
  sendOSC("/TEST", channel, command, value);
}

void oscEvent(OscMessage theOscMessage) 
{
  String addrPattern = split(theOscMessage.addrPattern(), '/')[1];
  String typeTag = theOscMessage.typetag();
  //ArrayList messages = new ArrayList();
  
  /* print the address pattern and the typetag of the received OscMessage */
  /*println("### received an osc message.");
  println(" addrpattern: "+theOscMessage.addrPattern());
  println(" typetag: "+theOscMessage.typetag());*/
  
  
  //messages.add(theOscMessage.get(0).intValue());
  //for(int m = 0; m < theOscMessage.size(); m++)
  
  switch(AddressPattern.toAP(addrPattern)) 
  {
    case TEST: 
      println("Testing:");
      break;
    case mel: 
      println("Melody recieved");
      //messages.add(theOscMessage.get(1).intValue());
      System.out.println("Built Mel on: "+theOscMessage.get(0).intValue());
      myParticleSystem.AddParticle("Melody", (float)theOscMessage.get(1).intValue());
      break;
    case dyn: 
      println("Dynamics revieved"); 
      myParticleSystem.AddParticle("Dynamics");
      break;
    case rhy: 
      println("Rhythm revieved"); 
      myParticleSystem.AddParticle("Rhythm");
      break;
    case chord: 
      println("Chord Progression revieved"); 
      
      if (theOscMessage.get(1).intValue() == 1)
      {
        myParticleSystem.AddParticle("ChordProgressionThree", theOscMessage.get(0).intValue());
      }
      else if (theOscMessage.get(1).intValue() == 2)
      {
        myParticleSystem.AddParticle("ChordProgressionFour", theOscMessage.get(0).intValue());
      }
      else
      {
        myParticleSystem.AddParticle("ChordProgressionFive", theOscMessage.get(0).intValue());
      }
      //messages.add(theOscMessage.get(1).intValue());
      break;
    default:
    println("Recieved unknown type: " + theOscMessage.addrPattern());
    break;
  }
}

void drawNodes()
{
  shape(chordNodeSprite, chordNodePosition.x, ScreenSize.y/2  - chordNodeSprite.height / 3);
  shape(dynamicsNodeSprite, dynamicsNodePosition.x, ScreenSize.y/2  - dynamicsNodeSprite.height / 3);
  shape(melodyNodeSprite, melodyNodePosition.x, ScreenSize.y/2  - melodyNodeSprite.height / 3);
  shape(rhythmNodeSprite, rhythmNodePosition.x, ScreenSize.y/2  - rhythmNodeSprite.height / 3);
}

void drawParticles()
{
  
  int r = 10;  //temp
  float angle;
  for (int i = 0; i < myParticleSystem.particles.size(); i++)
  {
    //drawBall(myParticleSystem.particles.get(i).Position);
    Particle myParticle = myParticleSystem.particles.get(i);
    PVector myPPos = myParticle.GetPosition();
    PVector myPVel = myParticle.GetVelocity();
    PVector myPSVel = myParticle.GetStaticVelocity();
    //shape myShape;
    if (myParticle.Active)
    {
      float oldWidth;
      float oldHeight;
      switch(ParticleTypes.getType(myParticle.GetType().toString())) 
      {
        
      case ChordProgressionThree: 
        pushMatrix();
        oldWidth = chordThreeSprite.width;
        oldHeight = chordThreeSprite.height;
        translate ( scaleDiscrepancy * myPPos.x - chordThreeSprite.width / 2, scaleDiscrepancy * myPPos.y - chordThreeSprite.height / 2);
        angle = atan2( myPVel.y * deltaTime + myPSVel.y , myPVel.x * deltaTime + myPSVel.x );
        rotate(angle - degrees(90.005f));
        shape(chordThreeSprite, -chordThreeSprite.width/2 * (scaleDiscrepancy / 2), -chordThreeSprite.height * (scaleDiscrepancy / 2));
        popMatrix();
        break;
      case ChordProgressionFour: 
        pushMatrix();
        
        translate ( scaleDiscrepancy * myPPos.x -  chordFourSprite.width / 2, scaleDiscrepancy * myPPos.y -  chordFourSprite.height / 2);
        angle = atan2( myPVel.y * deltaTime + myPSVel.y , myPVel.x * deltaTime + myPSVel.x );
        rotate(angle - degrees(90.005f));
        shape(chordFourSprite,  -chordFourSprite.width * (scaleDiscrepancy / 2), -chordFourSprite.height * (scaleDiscrepancy / 2));
        popMatrix();
        break;
      case ChordProgressionFive: 
        pushMatrix();
        
        translate ( scaleDiscrepancy * myPPos.x -  chordFiveSprite.width / 2, scaleDiscrepancy * myPPos.y -  chordFiveSprite.height / 2);
        angle = atan2( myPVel.y + myPSVel.y , myPVel.x + myPSVel.x );
        rotate(angle - degrees(90.005f));
        shape(chordFiveSprite, -chordFiveSprite.width * (scaleDiscrepancy / 2), -chordFiveSprite.height * (scaleDiscrepancy / 2));
        popMatrix();
        break;
      case Melody: 
        pushMatrix();
        scale(1/myParticle.GetScale(), 1/myParticle.GetScale());
        translate(scaleDiscrepancy * myPPos.x * myParticle.GetScale(), scaleDiscrepancy * myPPos.y * myParticle.GetScale());
        shape(melodySprite, -melodySprite.width * (scaleDiscrepancy / 2),  -melodySprite.height * (scaleDiscrepancy / 2));
        popMatrix();
        break;
      case Dynamics: 
        stroke(86,30,88);
        fill(145,38,143,70);
        ellipse(scaleDiscrepancy * myPPos.x, scaleDiscrepancy * myPPos.y,r,r);
        break;
      case Rhythm: 
        shape(rhythmSprite, (scaleDiscrepancy * myPPos.x - rhythmSprite.width * (scaleDiscrepancy / 2)),( scaleDiscrepancy * myPPos.y  - rhythmSprite.height * (scaleDiscrepancy / 2)));
        break;
      case NULL: 
        stroke(0);
        fill(0,100);
        ellipse(myPPos.x, myPPos.y, r,r);
        break;
      default:
        System.out.println("Unknown particle type: " + myParticle.GetType().toString());
        break;
      }
    }
  }
}

void drawBall(PVector pos)
{
  
  int r = 10; //temp
    stroke(0);
    fill(0,100);
    ellipse(pos.x,pos.y ,r,r);
}

void displaySplashScreen()
{
  
}

public class SubParticle
{
  private float scale = 1f;
  private float alpha = 255f;
  private float lifetime = 1f;
  
  public PVector Position; 
  
  private SubParticleTypes type = SubParticleTypes.NULL;
  
  public void dispay()
  {
    
  }
  public void setType(SubParticleTypes newType)
  {
    type = newType;
  }
  
  public void setType(String newType)
  {
    switch(SubParticleTypes.getType(newType)) 
    {
    case RhythmPulse: 
      setType(SubParticleTypes.RhythmPulse);
      break;
    case MelodyBead: 
      setType(SubParticleTypes.MelodyBead);
      break;
    case NULL: 
      break;
    default:
      System.out.println("Unknown particle type: " + type);
      setType(SubParticleTypes.NULL);
      break;
    }
    
  }
}
