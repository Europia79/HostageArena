package mc.euro.extraction.stats;

/**
 * This class defines wins, losses, and ties <br/><br/>
 * 
 * WIN = Hostage was Extracted Successfully. <br/>
 * LOSS = Hostage was killed. <br/>
 *
 * @author Nikolai
 */
public class OUTCOME {
    
    public static String hostageExtracted() {
        return "WIN";
    }
    
    public static String hostageKilled() {
        return "LOSS";
    }
    
}
