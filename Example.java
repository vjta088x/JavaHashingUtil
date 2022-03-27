package me.vjta088x.sha;

public class Example {
    public static void main(String[] args) {
        new Example();
    }

    public Example() {
        //computes hash without salt for input test outputs string with prefix
        String withPrefix = new HashingUtil("test").withAlgorithm(HashingUtil.AlgorithmType.SHA512).asStringWithPrefix();

        //outputs with: $SHA512$0$.ee26b0dd4af7e749aa1a8ee3c10ae9923f618980772e473f8819a5d4940e0db27ac185f8a0e1d5f84f88bc887fd67b143732c304cc5fa9ad8e6f57f50028a8ff
        System.out.println("with: " + withPrefix);

        //computes hash without salt for input test outputs string without prefix
        String withoutPrefix = new HashingUtil("test").withAlgorithm(HashingUtil.AlgorithmType.SHA512).asString();

        //outputs without: ee26b0dd4af7e749aa1a8ee3c10ae9923f618980772e473f8819a5d4940e0db27ac185f8a0e1d5f84f88bc887fd67b143732c304cc5fa9ad8e6f57f50028a8ff
        System.out.println("without: " + withoutPrefix);

        //compares unhashed string against hashed
        System.out.println(new HashingUtil().compareString(withPrefix, "test")); //returns true

        System.out.println(new HashingUtil().compareString(withPrefix, "test222")); //returns false

        //computes hash with salt of 12 bytes, salt hash 10 times with usage of md5 algorithm
        String hash2 = new HashingUtil("ultraSecretPassword").withSalt(12,10).withAlgorithm(HashingUtil.AlgorithmType.MD5).asStringWithPrefix();

        //outputs different string every time depending on generated salt
        System.out.println(hash2);
    }
}
