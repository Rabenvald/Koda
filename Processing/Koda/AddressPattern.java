public enum AddressPattern
{
    mel, dyn, rhy, chord,
    TIME,
    TEST, NOVALUE;

    public static AddressPattern toAP(String str)
    {
        try 
        {
            return valueOf(str);
        } 
        catch (Exception ex) 
        {
            return NOVALUE;
        }
    }   
}
