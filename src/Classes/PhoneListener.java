package Classes;

import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

public class PhoneListener extends PhoneStateListener
{
	  @Override  
      public void onCallStateChanged(int state, String incomingNumber) 
	  {  
		  switch(state)
		  {  
	          case TelephonyManager.CALL_STATE_RINGING:  
	              break;  
	          case TelephonyManager.CALL_STATE_IDLE:  
	              break;  
	          case TelephonyManager.CALL_STATE_OFFHOOK:  
	              break;  
          }
		  
          super.onCallStateChanged(state, incomingNumber); 
      }  
}
