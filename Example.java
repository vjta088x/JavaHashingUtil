package me.vjta088x.sha;

public class Example {
    public static void main(String[] args) {
        new Example();
    }

    public Example() {
        //computes hash without salt for input test
        String hash = new HashingUtil("test").computeStringWithAlgorithm(HashingUtil.AlgorithmType.SHA512);

        System.out.println("hash: " + hash);

        //compares unhashed string against hashed
        System.out.println(new HashingUtil().compareString(hash, "test")); //returns true

        System.out.println(new HashingUtil().compareString(hash, "test222")); //returns false

        //computes hash with salt of 12 bytes, salt hash 10 times with usage of md5 algorithm
        String hash2 = new HashingUtil("ultraSecretPassword").withSalt(12,10).computeStringWithAlgorithm(HashingUtil.AlgorithmType.MD5);
        System.out.println(hash2);
    }
}
