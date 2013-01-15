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
public PVector ScreenSize = new PVector (1000, 600);

private PShape chordThreeSprite;
private PShape chordFourSprite;
private PShape chordFiveSprite;
private PShape melodySprite;

private PShape chordNodeSprite;
private PShape dynamicsNodeSprite;
private PShape melodyNodeSprite;
private PShape rhythmNodeSprite;

public UTParticleSystem myParticleSystem;
public int AvailableCores = Runtime.getRuntime().availableProcessors();

void setup()
{
  size((int)ScreenSize.x, (int)ScreenSize.y);
  frameRate(60);
  oscP5 = new OscP5(this, LOCAL_PORT);
  myRemoteLocation = new NetAddress("127.0.0.1", LOCAL_PORT);
  //oscRemoteLocation = new NetAddress("127.0.0.1",24717);
  loadAssets();
  myParticleSystem = new UTParticleSystem(true, this, ScreenSize); //fixhere
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
     
     chordNodeSprite = loadShape ("/nodes/chordnode.svg");
     dynamicsNodeSprite = loadShape ("/nodes/dynamicsnode.svg");
     melodyNodeSprite = loadShape ("/nodes/melodynode.svg");
     rhythmNodeSprite = loadShape ("/nodes/rhythmnode.svg");
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
  //println ("dT: " + deltaTime);
  //println("x:"+floor(mouseX)+" y:"+floor(mouseY));
  myParticleSystem.simulate();
  drawParticles();
  drawNodes();
  lastTime = currentTime; 
}

void keyPressed() 
{
  if (key == CODED) 
  {
    if (keyCode == UP) 
    {
      sendOSC("/chord",(int)random(255),(int)random(255),(int)random(255)); 
    } 
    else if (keyCode == DOWN) 
    {
      sendOSC("/dyn",(int)random(255),(int)random(255),(int)random(255));
    } 
    else if (keyCode == RIGHT) 
    {
      sendOSC("/mel",(int)random(255),(int)random(255),(int)random(255));
    } 
    else if (keyCode == LEFT) 
    {
      sendOSC("/rhy",(int)random(255),(int)random(255),(int)random(255));  
    } 
    else if (keyCode == CONTROL) 
    {
      sendOSC((int)random(255),(int)random(255),(int)currentTime); 
    } 
  } 
}

void sendOSC(String addrPattern, int channel, int command, int value)
{
  OscMessage myMessage = new OscMessage(addrPattern);
  myMessage.add(channel+1);          //identifier for routing in PD
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
  ArrayList messages = new ArrayList();
  
  /* print the address pattern and the typetag of the received OscMessage */
  println("### received an osc message.");
  println(" addrpattern: "+theOscMessage.addrPattern());
  println(" typetag: "+theOscMessage.typetag());
  
  
  messages.add(theOscMessage.get(0).intValue());
  //for(int m = 0; m < theOscMessage.size(); m++)
  
  switch(AddressPattern.toAP(addrPattern)) 
  {
    case TEST: 
      println("Testing:");
      break;
    case mel: 
      println("Melody recieved");
      messages.add(theOscMessage.get(1).intValue());
      myParticleSystem.AddParticle("Melody");
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
      myParticleSystem.AddParticle("ChordProgression");
      if (messages.get(0).equals(1))
      {
        
      }
      else if (messages.get(0).equals(2))
      {
        
      }
      else
      {
        
      }
      //messages.add(theOscMessage.get(1).intValue());
      break;
    default:
    println("Recieved unknown type: " + theOscMessage.addrPattern());
    break;
  }
}

void drawNodes()
{/*chordNodeSprite = loadShape ("/nodes/chordnode.svg");
     dynamicsNodeSprite = loadShape ("/nodes/dynamicsnode.svg");
     melodyNodeSprite = loadShape ("/nodes/melodynode.svg");
     rhythmNodeSprite*/
  //shape(chordNodeSprite, ScreenSize.x/4 - chordNodeSprite.width / 2, ScreenSize.y/2  - chordNodeSprite.height / 2);
}

void drawParticles()
{
  int r = 10;  //temp
  for (int i = 0; i < myParticleSystem.particles.size(); i++)
  {
    //drawBall(myParticleSystem.particles.get(i).Position);
    Particle myParticle = myParticleSystem.particles.get(i);
    if (myParticle.Active)
    {
      switch(ParticleTypes.getType(myParticle.GetType().toString())) 
      {
      case ChordProgression: 
        shape(chordThreeSprite, myParticle.Position.x - chordThreeSprite.width / 2, myParticle.Position.y  - chordThreeSprite.height / 2);
        /*stroke(153,181,59);
        fill(168,207,55,70);
        ellipse(myParticle.Position.x,myParticle.Position.y,r,r);*/
        break;
      case Melody: 
        shape(melodySprite, myParticle.Position.x - melodySprite.width / 2, myParticle.Position.y  - melodySprite.height / 2);
        /*stroke(30,141,169);
        fill(39,169,225,70);
        ellipse(myParticle.Position.x,myParticle.Position.y,r,r);*/
        break;
      case Dynamics: 
        stroke(86,30,88);
        fill(145,38,143,70);
        ellipse(myParticle.Position.x,myParticle.Position.y,r,r);
        break;
      case Rhythm: 
        stroke(177,36,120);
        fill(237,42,123,70);
        ellipse(myParticle.Position.x,myParticle.Position.y,r,r);
        break;
      case NULL: 
        stroke(0);
        fill(0,100);
        ellipse(myParticle.Position.x,myParticle.Position.y, r,r);
        break;
      default:
        System.out.println("Unknown particle type: " + myParticle.GetType().toString());
        break;
      }
    }
  }
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
    case Rythm: 
      System.out.println("Rythm revieved"); 
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
}

void drawBall(PVector pos)
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
    case Rythm: 
      System.out.println("Rythm revieved"); 
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
  
  int r = 10; //temp
    stroke(0);
    fill(0,100);
    ellipse(pos.x,pos.y ,r,r);
}

void displaySplashScreen()
{
  
}
