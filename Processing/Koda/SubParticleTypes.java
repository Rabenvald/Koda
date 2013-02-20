public enum SubParticleTypes
{
  RhythmPulse,
  MelodyBead,
  NULL;
  
  public static SubParticleTypes getType(String str)
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
