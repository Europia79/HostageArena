package mc.euro.extraction.objects;

/**
 * A different implementation using a bounding box (more efficient). <br/>
 * 
 * <pre>
 * The normal algorithm that determines if a hostage is inside the ExtractionZone 
 * is simply to calculate the distance between the hostage & the center of the 
 * extraction zone (the extraction point), and then compare that distance with 
 * the radius of the extraction zone.
 * 
 * Using a bounding box will be more efficient because we won't have to use 
 * an expensive square root calculation. We'll just have to do a boolean comparison 
 * on the X, Y, & Z values.
 * </pre>
 * 
 * @author Nikolai
 */
public class ExtractionZone {
    
}
