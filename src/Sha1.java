/****************************************************************************************
 * NAME: SAMI AHMAD KHAN
 * CWID: A20352677
 * COURSE: CS 550: ADVANCE OPERATING SYSTEMS
 * PROGRAMMING ASSIGNMENT NO.: 03																   
 * TOPIC: "A Decentralized P2P system implementing a Distributed Hash Table"
 * @author SamAK
 * 
*****************************************************************************************/
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/*-------------------------------------------------------------------------------------------------------------------------------------------
 * CLASS NAME: Sha1
 * FUNCTIONS: 1. This function takes the "Key" from the server and performs sha-1 hashing on it and returns it back
 * METHODS USED: 1.hashing() - (String)
 *-------------------------------------------------------------------------------------------------------------------------------------------
 */	
public class Sha1 {
	

	public String hashing(String key){ 
		String sha1 = null;
		StringBuffer sb = new StringBuffer();
		try{
			MessageDigest mDigest = MessageDigest.getInstance("SHA1");
			byte[] result = mDigest.digest(key.getBytes());
			
			for (int i = 0; i < result.length; i++) {
	            sb.append(Integer.toString((result[i] & 0xff) + 0x100, 16).substring(1));
	        }
		} 
		catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return sb.toString();
		
	}
}
	
