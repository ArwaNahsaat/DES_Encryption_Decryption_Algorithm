import javafx.util.Pair;
import jdk.nashorn.internal.runtime.regexp.joni.ast.StringNode;

import java.util.Vector;

public class DES {

    public static int [] pc1 = {57,49,41,33,25,17,9,1,58,50,42,34,26,18,10,2,59,51,43,35,27,19,11,3,60,52,44,36,63,55,47,39,31,23,15,7,62,54,46,38,30,22,14,6,61,53,45,37,29,21,13,5,28,20,12,4};
    public static int []pc2 = {14,17,11,24,1,5,3,28,15,6,21,10,23,19,12,4,26,8,16,7,27,20,13,2,41,52,31,37,47,55,30,40,51,45,33,48,44,49,39,56,34,53,46,42,50,36,29,32};
    public static int []ip = { 58,50,42,34,26,18,10,2,60,52,44,36,28,20,12,4,62,54,46,38,30,22,14,6,64,56,48,40,32,24,16,8,57,49,41,33,25,17,9,1,59,51,43,35,27,19,11,3,61,53,45,37,29,21,13,5,63,55,47,39,31,23,15,7};
    public static int []expansion = {32,1,2,3,4,5,4,5,6,7,8,9,8,9,10,11,12,13,12,13,14,15,16,17,16,17,18,19,20,21,20,21,22,23,24,25,24,25,26,27,28,29,28,29,30,31,32,1};
    public static int []p = {16,7,20,21,29,12,28,17,1,15,23,26,5,18,31,10,2,8,24,14,32,27,3,9,19,13,30,6,22,11,4,25};
    public static int []finalP = {40,8,48,16,56,24,64,32,39,7,47,15,55,23,63,31,38,6,46,14,54,22,62,30,37,5,45,13,53,21,61,29,36,4,44,12,52,20,60,28,35,3,43,11,51,19,59,27,34,2,42,10,50,18,58,26,33,1,41,9,49,17,57,25};

    public static int [][][]sBox = {{{14,4,13,1,2,15,11,8,3,10,6,12,5,9,0,7},
        {0,15,7,4,14,2,13,1,10,6,12,11,9,5,3,8},
        {4,1,14,8,13,6,2,11,15,12,9,7,3,10,5,0},
        {15,12,8,2,4,9,1,7,5,11,3,14,10,0,6,13}},

        {{15,1,8,14,6,11,3,4,9,7,2,13,12,0,5,10},
            {3,13,4,7,15,2,8,14,12,0,1,10,6,9,11,5},
            {0,14,7,11,10,4,13,1,5,8,12,6,9,3,2,15},
            {13,8,10,1,3,15,4,2,11,6,7,12,0,5,14,9}},

        {{10,0,9,14,6,3,15,5,1,13,12,7,11,4,2,8},
            {13,7,0,9,3,4,6,10,2,8,5,14,12,11,15,1},
            {13,6,4,9,8,15,3,0,11,1,2,12,5,10,14,7},
            {1,10,13,0,6,9,8,7,4,15,14,3,11,5,2,12}},

        {{7,13,14,3,0,6,9,10,1,2,8,5,11,12,4,15},
            {13,8,11,5,6,15,0,3,4,7,2,12,1,10,14,9},
            {10,6,9,0,12,11,7,13,15,1,3,14,5,2,8,4},
            {3,15,0,6,10,1,13,8,9,4,5,11,12,7,2,14}},

        {{2,12,4,1,7,10,11,6,8,5,3,15,13,0,14,9},
            {14,11,2,12,4,7,13,1,5,0,15,10,3,9,8,6},
            {4,2,1,11,10,13,7,8,15,9,12,5,6,3,0,14},
            {11,8,12,7,1,14,2,13,6,15,0,9,10,4,5,3}},

        {{12,1,10,15,9,2,6,8,0,13,3,4,14,7,5,11},
            {10,15,4,2,7,12,9,5,6,1,13,14,0,11,3,8},
            {9,14,15,5,2,8,12,3,7,0,4,10,1,13,11,6},
            {4,3,2,12,9,5,15,10,11,14,1,7,6,0,8,13}},

        {{4,11,2,14,15,0,8,13,3,12,9,7,5,10,6,1},
            {13,0,11,7,4,9,1,10,14,3,5,12,2,15,8,6},
            {1,4,11,13,12,3,7,14,10,15,6,8,0,5,9,2},
            {6,11,13,8,1,4,10,7,9,5,0,15,14,2,3,12}},

        {{13,2,8,4,6,15,11,1,10,9,3,14,5,0,12,7},
            {1,15,13,8,10,3,7,4,12,5,6,11,0,14,9,2},
            {7,11,4,1,9,12,14,2,0,6,10,13,15,3,5,8},
            {2,1,14,7,4,10,8,13,15,12,9,0,3,5,6,11}}};


    public static String plainText = "0000000100100011010001010110011110001001101010111100110111101111";

    public static Vector<String> permuteKeys(Vector<String>keys){

        for(int i=1; i<17; i++){
            String finalKeyI = "";
            for(int j=0;j<48;++j)
                finalKeyI += keys.get(i).charAt(pc2[j]-1);

            keys.set(i, finalKeyI);
        }

        return keys;
    }

    public static Vector<String> generateKeys(String key){
        String keyPermuted="";
        Vector<String>keys = new Vector<>();
        for(int i=0; i<56; i++)
            keyPermuted += key.charAt(pc1[i]-1);

        String L0 = keyPermuted.substring(0,28);
        String R0 = keyPermuted.substring(28,56);

        keys.add(L0+R0);

        for(int i=1; i<17; i++) {
            String Lefti = keys.get(i - 1).substring(0,28);
            String Righti = keys.get(i - 1).substring(28,56);

            if (i == 1 || i == 2 || i == 9 || i == 16){
                Lefti = Lefti.substring(1) + Lefti.substring(0, 1);
                Righti = Righti.substring(1) + Righti.substring(0, 1);
            }
            else{
                Lefti = Lefti.substring(2) + Lefti.substring(0, 2);
                Righti = Righti.substring(2) + Righti.substring(0, 2);
            }

            keys.add(Lefti+Righti);
        }

        keys = permuteKeys(keys);
        return keys;
    }

    public static String expand(String previousR){
        String R = "";
        for(int i=0; i<48; i++)
            R += previousR.charAt(expansion[i]-1);
        return R;
    }

    public static String xor(String s1, String s2){
        String result = "";
        for(int i=0; i<s1.length(); i++)
            result += s1.charAt(i) ^ s2.charAt(i);
        return result;
    }

    public static String permuteSBoxOutput(String shrinkedR){
        String permutedSboxOutput = "";

        for (int i=0; i<32; i++)
            permutedSboxOutput += shrinkedR.charAt(p[i] - 1);

        return permutedSboxOutput;
    }
    public static String applySBox(String R){
        String shrinkedR = "";
        for(int j=0,sBoxi=0; j<48; j+=6,sBoxi++){
            char innerBits = '0';

            innerBits += (R.charAt(j)-'0')<<1;
            innerBits += (R.charAt(j+5)-'0');
            int row = innerBits - '0';

            char outerBits = '0';
            outerBits += (R.charAt(j+1)-'0')<<3;
            outerBits += (R.charAt(j+2)-'0')<<2;
            outerBits += (R.charAt(j+3)-'0')<<1;
            outerBits += (R.charAt(j+4)-'0');
            int col = outerBits - '0';

            int number = sBox[sBoxi][row][col];
            String binary = Integer.toString(number, 2);
            int len = 4-binary.length();
            while(len!=0)
            {
                shrinkedR+='0';
                len--;
            }
            shrinkedR += binary;
        }

        shrinkedR = permuteSBoxOutput(shrinkedR);
        return shrinkedR;
    }

    public static Vector<Pair<String, String>> calculateRightLeftEncrypt(Vector<Pair<String, String>> LeftRightVector, Vector<String> keys){
        String L = "";
        String R = "";
        for(int i=1; i<17; i++){
            L = LeftRightVector.get(i-1).getValue();
            String expandedR = expand(LeftRightVector.get(i-1).getValue());

            R =  xor(keys.get(i), expandedR);
            R = applySBox(R);
            R =  xor(LeftRightVector.get(i-1).getKey(),R);

            LeftRightVector.add(new Pair<>(L,R));
        }
        return LeftRightVector;
    }

    public static Vector<Pair<String, String>> calculateRightLeftDecrypt(Vector<Pair<String, String>> LeftRightVector, Vector<String> keys){
        String L = "";
        String R = "";
        for(int i=16,j=1; i>=1; i--,j++){
            L = LeftRightVector.get(j-1).getValue();
            String expandedR = expand(LeftRightVector.get(j-1).getValue());

            R =  xor(keys.get(i), expandedR);
            R = applySBox(R);
            R =  xor(LeftRightVector.get(j-1).getKey(),R);

            LeftRightVector.add(new Pair<>(L,R));
        }
        return LeftRightVector;
    }

    public static String finalPermutation(String encryptedMsg){
        String permutedEncMsg = "";
        for(int i=0; i<64; i++)
            permutedEncMsg += encryptedMsg.charAt(finalP[i]-1);

        return permutedEncMsg;
    }

    public static String initialPermutation(String text){
        String permutedText = "";
        for(int i=0; i<64; i++)
            permutedText += text.charAt(ip[i]-1);

        return permutedText;
    }

    public static String split(String text, int startPoint, int endPoint){
        return text.substring(startPoint,endPoint);
    }

    public static String swap(String text,Vector<Pair<String, String>> LeftRightVector){
        text = LeftRightVector.get(16).getValue();
        text += LeftRightVector.get(16).getKey();
        return text;
    }
    public static String encrypt(String plaintext,Vector<String>keys){
        String encryptedMsg = "";

        encryptedMsg = initialPermutation( plaintext);

        String L = split(encryptedMsg,0,32);
        String R = split(encryptedMsg,32,64);

        Vector<Pair<String, String>> LeftRightVector = new Vector<>();
        LeftRightVector.add(new Pair<>(L,R));
        calculateRightLeftEncrypt(LeftRightVector, keys);

        encryptedMsg = swap(encryptedMsg, LeftRightVector);

        encryptedMsg = finalPermutation(encryptedMsg);
        return encryptedMsg;
    }

    public static String decrypt(String encryptedMsg,Vector<String> keys){
        String plainText = "";
        plainText = initialPermutation(encryptedMsg);

        String L = split(plainText,0,32);
        String R = split(plainText,32,64);

        Vector<Pair<String, String>> LeftRightVector = new Vector<>();
        LeftRightVector.add(new Pair<>(L,R));
        calculateRightLeftDecrypt(LeftRightVector, keys);

        plainText = swap(plainText,LeftRightVector);
        plainText = finalPermutation(plainText);

        return plainText;
    }

    public static void main(String[] args) {
        String key = "0001001100110100010101110111100110011011101111001101111111110001";
        System.out.println("plain text   =   "+plainText);

        Vector<String>keys = generateKeys(key);
        String encryptedMsg = encrypt(plainText,keys);
        System.out.println("Encrypted Text = "+encryptedMsg);

        String decryptedMsg = decrypt(encryptedMsg,keys);
        System.out.println("Decrypted Text = "+decryptedMsg);

        if(plainText.equals(decryptedMsg))
            System.out.println("SUCCESSFULLY DECRYPTED THE MESSGAE");
        else
            System.out.println("DECRYPTION FAILED!!!");
    }
}
