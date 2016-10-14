package org.ligi.satoshiproof.util

import com.google.bitcoin.core.Base58
import org.spongycastle.crypto.digests.RIPEMD160Digest
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

object AddressGenerator {

    fun dataToAddressString(data: ByteArray): String {
        try {
            val sha256Digest = MessageDigest.getInstance("SHA-256")
            val ripemd160Digest = RIPEMD160Digest()

            val sha256 = sha256Digest.digest(data)
            ripemd160Digest.update(sha256, 0, sha256.size)
            val bytes = ByteArray(20)
            ripemd160Digest.doFinal(bytes, 0)

            val addressBytes = ByteArray(1 + bytes.size + 4)
            System.arraycopy(bytes, 0, addressBytes, 1, bytes.size)
            val check = doubleDigest(addressBytes, 0, bytes.size + 1, sha256Digest)
            System.arraycopy(check, 0, addressBytes, bytes.size + 1, 4)
            return Base58.encode(addressBytes)

        } catch (e: NoSuchAlgorithmException) {
            throw RuntimeException(e)  // Cannot happen.
        }

    }

    private fun doubleDigest(input: ByteArray, offset: Int, length: Int, digest: MessageDigest): ByteArray {
        digest.reset()
        digest.update(input, offset, length)
        val first = digest.digest()
        return digest.digest(first)

    }
}
