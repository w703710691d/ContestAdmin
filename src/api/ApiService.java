package api;

public class ApiService 
{
    static  boolean CheckAscii(String s)
    {
        for(char c:s.toCharArray())
        {
            if(c > 127) return false;
        }
        return true;
    }
}
