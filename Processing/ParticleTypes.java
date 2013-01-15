public enum ParticleTypes
{
  Melody,
  ChordProgression,
  ChordProgressionThree,
  ChordProgressionFour,
  ChordProgressionFive,
  Rhythm,
  Dynamics,
  NULL;
  
  public static ParticleTypes getType(String str)
    {
        try 
        {
            return valueOf(str);
        } 
        catch (Exception ex) 
        {
            return NULL;
        }
    }   
}
