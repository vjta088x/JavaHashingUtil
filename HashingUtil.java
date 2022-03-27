package me.vjta088x.sha;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Random;

public class HashingUtil {
    byte[] toHash;

    private AlgorithmType algorithmType;

    private int saltLength = 0;
    private int saltCycles = 0;

    private String pepperPassword = "";
    private int pepperCycles = 0;

    private Random random = new Random();

    public HashingUtil(byte[] toHash) {
        this.toHash = toHash;
    }
    public HashingUtil(String toHash){
        this.toHash = toHash.getBytes(StandardCharsets.UTF_8);
    }
    public HashingUtil(){
        this.toHash = new byte[0];
        algorithmType = AlgorithmType.SHA512;
    }

    public HashingUtil withSalt(int saltLength, int saltingCycles){
        this.saltLength = saltLength;
        this.saltCycles = saltingCycles;
        return this;
    }

    //TODO: Pepper
   /* public HashingUtil withPepper(String pepperPassword, int pepperCycles){
        this.pepperPassword = pepperPassword;
        this.pepperCycles = pepperCycles;
        return this;
    }*/

    public String computeStringWithAlgorithm(AlgorithmType algorithmType){
        this.algorithmType = algorithmType;
        return computeStringHash();
    }

    public byte[] computeBytesWithAlgorithm(AlgorithmType algorithmType){
        this.algorithmType = algorithmType;
        return computeByteHash();
    }

    public boolean compareString(String hashed, String unHashed)
    {
        HashedModel hashedModel = new HashedModel(hashed);
        byte[] unHashedHash = computeByteHash(hashedModel.getSalt(), hashedModel.getSaltCycles(), unHashed.getBytes(StandardCharsets.UTF_8), hashedModel.getAlgorithmType());
        return Arrays.equals(hashedModel.getHash(), unHashedHash);
    }


    private String computeStringHash(){
        byte[] salt = generateSalt(saltLength);
        String result = byteArrayToHex(computeByteHash(salt, this.saltCycles, this.toHash, this.algorithmType));
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(String.format("%s$%d$%s.%s", algorithmType.prefix, saltCycles, byteArrayToHex(salt), result));
        return stringBuilder.toString();
    }

    private byte[] computeByteHash(byte[] salt, int saltCycles, byte[] toHash, AlgorithmType algorithmType){
        try{
            MessageDigest digest = MessageDigest.getInstance(algorithmType.name);
            byte[] lastHashed = toHash.clone();

            if(saltCycles > 0)
            {
                for (int i = 0; i < saltCycles; i ++){
                    lastHashed = digest.digest(saltBytes(lastHashed, salt));
                }
            }else{
                lastHashed = digest.digest(lastHashed);
            }
            return lastHashed;
        }catch (NoSuchAlgorithmException e)
        {
            e.printStackTrace();
        }
        return new byte[]{};
    }

    private byte[] computeByteHash(){
        return computeByteHash(generateSalt(saltLength), this.saltCycles, this.toHash, this.algorithmType);
    }

    private byte[] generateSalt(int saltLength){
        byte[] array = new byte[saltLength];
        random.nextBytes(array);
        return array;
    }
    private byte[] saltBytes(byte[] bytes, byte[] salt){
        byte[] newByteArray = new byte[bytes.length + salt.length];
        int lastIndex = 0;
        for (int i = 0; i < salt.length; i++) {
            newByteArray[i] = salt[i];
            lastIndex = i;
        }
        for (byte aByte : bytes) {
            lastIndex ++;
            newByteArray[lastIndex] = aByte;
        }
        return newByteArray;
    }

    public static String byteArrayToHex(byte[] input){
        StringBuilder sb = new StringBuilder(input.length * 2);
        for (byte b : input) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    public static byte[] hexToByteArray(String input){
        byte[] array = new byte[input.length() / 2];
        int index;
        for (int i = 0; i < array.length; i++) {
            index = i * 2;
            int data = Integer.parseInt(input.substring(index, index + 2), 16);
            array[i] = (byte)data;
        }
        return array;
    }

    public class HashedModel{
        private byte[] hash;
        private byte[] salt;
        private int saltCycles;
        private AlgorithmType algorithmType;

        public HashedModel(String hashed) {
            String[] split = hashed.split("\\$");
            try{
                algorithmType = AlgorithmType.valueOf(split[1]);
            }catch (IllegalArgumentException e)
            {
                throw new IllegalStateException("No Hashing algorithm was found");
            }
            saltCycles = Integer.parseInt(split[2]);

            String[] hashSplit = split[3].split("\\.");

            salt =  HashingUtil.hexToByteArray(hashSplit[0]);
            hash = HashingUtil.hexToByteArray(hashSplit[1]);
        }

        public HashedModel(byte[] hash, byte[] salt, AlgorithmType algorithmType) {
            this.hash = hash;
            this.salt = salt;
            this.algorithmType = algorithmType;
        }

        public byte[] getHash() {
            return hash;
        }

        public void setHash(byte[] hash) {
            this.hash = hash;
        }

        public byte[] getSalt() {
            return salt;
        }

        public void setSalt(byte[] salt) {
            this.salt = salt;
        }

        public AlgorithmType getAlgorithmType() {
            return algorithmType;
        }

        public void setAlgorithmType(AlgorithmType algorithmType) {
            this.algorithmType = algorithmType;
        }

        public int getSaltCycles() {
            return saltCycles;
        }

        @Override
        public String toString() {
            return "HashedModel{" +
                    "hash=" + Arrays.toString(hash) +
                    ", salt=" + Arrays.toString(salt) +
                    ", saltLength=" + saltCycles +
                    ", algorithmType=" + algorithmType +
                    '}';
        }
    }

    public enum AlgorithmType{
        SHA512("$SHA512", "SHA-512"), SHA256("$SHA256","SHA-256"), MD5("$MD5","MD5"), BLOWFISH("$BLOWFISH", "BLOWFISH");
        private String prefix;
        private String name;

        AlgorithmType(String prefix, String name) {
            this.prefix = prefix;
            this.name = name;
        }

        public String getPrefix() {
            return prefix;
        }

        public String getName() {
            return name;
        }
    }
}
