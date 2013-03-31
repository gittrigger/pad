package com.pad;

import java.math.BigInteger;
import java.security.SecureRandom;

import android.util.Log;


class RSA
{
	final private static String TAG = "RSA";
	
        public BigInteger n, e, d;
        public int bitSize, groupSize;
        //private BigInteger d;

        public RSA(int bitlen)
        {
        	bitSize = bitlen;
        	groupSize = 4; // percentage of bit encryption size for max size equation can handle
        	
                if(bitlen == 0){return;}
                Log.i(TAG,"RSA \t"+ bitlen +" bit RSA Encryption...");
                SecureRandom r = new SecureRandom();
                Log.i(TAG,"RSA \tSecureRandom created...");
                BigInteger p = new BigInteger(bitlen / 2, 100, r);
                Log.i(TAG,"RSA \tp created...");
                BigInteger q = new BigInteger(bitlen / 2, 100, r);
                Log.i(TAG,"RSA \tq created...");
                n = p.multiply(q);
                Log.i(TAG,"RSA \tn (Shared Key) created...");

                BigInteger m = p.subtract(BigInteger.ONE).multiply(q.subtract(BigInteger.ONE));
                e = new BigInteger("3");
                while(m.gcd(e).intValue() > 1) e = e.add(new BigInteger("2"));
                Log.i(TAG,"RSA \te (Public Key) created...");

                d = e.modInverse(m);
                Log.i(TAG,"RSA \td (Private Key) created...");
        }

        public BigInteger encrypt(BigInteger message)
        {
                return message.modPow(e, n);
                // message^e mod n
        }
        
        public BigInteger encryptStr(String message)
       {
        	
                String numericString = "";
                String tempStr = "";
                int encLength = message.length();
                char ch;
                int intedch;
                for(int i = 0;i < encLength;i++){
                        ch = message.charAt(i);
                        intedch = (int) ch;
                        tempStr = "";
                        tempStr += intedch;
                        while(tempStr.length() < 3){tempStr = "0" + tempStr;}
                        numericString += tempStr;
                }
                Log.i(TAG,"RSA Message("+message.length()+") Ready To be Encrypted String(" +numericString.length() +"): " + numericString);
                BigInteger encryptedBIG = new BigInteger(numericString);
                return encryptedBIG.modPow(e, n);
        }
        public String encryptSafe(String message)
        {
        	int pSize = bitSize * groupSize;
        	pSize = pSize/100;
         	Log.i(TAG,"RSA Original bitSize("+bitSize+") groupSize("+groupSize+") pSize("+pSize+")");
         	if(pSize < 10){pSize = 10;}
         	String eData = "";
        	for(int a = 0; a < message.length(); a += pSize){
        		int t = a+pSize;
        		if( t > message.length() ){
        			t = message.length(); 
        		}
        		
        		String sS = message.substring(a, t);
        		
        		eData += encryptStr(sS) + "\n";
        	}
        	return eData;
         }

        public BigInteger decrypt(BigInteger message)
        {
                return message.modPow(d, n);
        }
        public String decryptToStr(BigInteger Emessage)
        {
        		Log.i(TAG,"RSA decryptToStr length(" +Emessage.toString().length() + ") " );
        		
                BigInteger Dmessage = Emessage.modPow(d, n);

                String numberString = Dmessage.toString();
                Log.i(TAG,"RSA Number String length(" + numberString.length() + ") /3(" + (numberString.length()/3) + ") %3(" + (3-(numberString.length()%3)) +")" );
                Log.i(TAG,"RSA Number String: " + numberString);
                
                String tempStr = "", decryptedStr = "";
                char ch,chedint;
                int tempInt;
                BigInteger tempBIG;
                for(int i = (3-(numberString.length()%3)); i > 0 && i < 3; i--){
                	Log.i(TAG,"RSA Appending 0 to numberString");
                	numberString = "0" + "" + numberString;
                }
                
                for(int i = 0;i+2 < numberString.length();i += 3){
                	
                	tempStr = numberString.substring(i, i+3);
                	tempBIG = new BigInteger(tempStr);
                	//tempBIG = new BigInteger(new String(numberString.substring(i, i+2)) );
                    //tempInt = tempBIG.intValue();
                    //chedint = (char) tempInt;
                    decryptedStr += (char) tempBIG.intValue();
                    Log.i(TAG,"RSA \tnum("+tempStr+") char("+(char) tempBIG.intValue() +") int("+tempBIG.intValue()+")");

                    //tempStr = "";
                }
                return decryptedStr;
        }
        
        public String decryptSafe(String eData){
        	Log.i(TAG,"RSA decryptSafe("+eData+")");
        	int nxtpos = 0;
        	String dData = "";
        	for(int b = 0; b < eData.length(); b=(eData.indexOf(10, b+1)) ){
        		nxtpos = eData.indexOf(10,b+1);
        		if( nxtpos < 0 ){
        			nxtpos = eData.length();
        		}
        		String c = eData.substring(b, nxtpos);
        		
        		c = c.trim();
        		
        		if( c.length() > 0 ){
        			Log.i(TAG,"RSA eData b("+b+") nxtpos("+nxtpos+") String(" + c +")");
        			try {
        				BigInteger bI = new BigInteger(c);
        				dData += decryptToStr(bI);
        			}
        			catch(NumberFormatException e){
        				Log.i(TAG,"RSA Caught NumberFormatException (Possibly user entered non-number data) " + e.getLocalizedMessage());
        				break;
        			}
        		}else{
        			break;
        		}
        		
        		if(nxtpos == eData.length() ){break;}
        	}
        	dData = dData.trim();
        	return dData;
        }
}


