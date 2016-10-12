package org.ligi.satoshiproof.util;

import com.google.bitcoin.core.Base58;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import org.spongycastle.crypto.digests.RIPEMD160Digest;

public class AddressGenerator {

    public static String dataToAddressString(final byte[] data) {
        try {
            final MessageDigest sha256Digest = MessageDigest.getInstance("SHA-256");
            final RIPEMD160Digest ripemd160Digest = new RIPEMD160Digest();

            byte[] sha256 = sha256Digest.digest(data);
            ripemd160Digest.update(sha256, 0, sha256.length);
            byte[] bytes = new byte[20];
            ripemd160Digest.doFinal(bytes, 0);

            byte[] addressBytes = new byte[1 + bytes.length + 4];
            System.arraycopy(bytes, 0, addressBytes, 1, bytes.length);
            byte[] check = doubleDigest(addressBytes, 0, bytes.length + 1, sha256Digest);
            System.arraycopy(check, 0, addressBytes, bytes.length + 1, 4);
            return Base58.encode(addressBytes);

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);  // Cannot happen.
        }
    }

    private static byte[] doubleDigest(byte[] input, int offset, int length, MessageDigest digest) {
        digest.reset();
        digest.update(input, offset, length);
        byte[] first = digest.digest();
        return digest.digest(first);

    }
}
