/**
 * Copyright 2011 Google Inc.

 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at

 * http://www.apache.org/licenses/LICENSE-2.0

 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.bitcoin.core

import java.io.UnsupportedEncodingException

/**
 *
 * Base58 is a way to encode Bitcoin addresses as numbers and letters. Note that this is not the same base58 as used by
 * Flickr, which you may see reference to around the internet.
 *
 * Satoshi says: why base-58 instead of standard base-64 encoding?
 *
 *
 *
 *  * Don't want 0OIl characters that look the same in some fonts and
 * could be used to create visually identical looking account numbers.
 *  * A string with non-alphanumeric characters is not as easily accepted as an account number.
 *  * E-mail usually won't line-break if there's no punctuation to break at.
 *  * Doubleclicking selects the whole number as one word if it's all alphanumeric.
 *
 */
object Base58 {
    private val ALPHABET = "123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz".toCharArray()

    private val INDEXES = IntArray(128)

    init {
        for (i in INDEXES.indices) {
            INDEXES[i] = -1
        }
        for (i in ALPHABET.indices) {
            INDEXES[ALPHABET[i].toInt()] = i
        }
    }

    /** Encodes the given bytes in base58. No checksum is appended.  */
    fun encode(input: ByteArray): String {
        var input = input
        if (input.size == 0) {
            return ""
        }
        input = copyOfRange(input, 0, input.size)
        // Count leading zeroes.
        var zeroCount = 0
        while (zeroCount < input.size && input[zeroCount].toInt() == 0) {
            ++zeroCount
        }
        // The actual encoding.
        val temp = ByteArray(input.size * 2)
        var j = temp.size

        var startAt = zeroCount
        while (startAt < input.size) {
            val mod = divmod58(input, startAt)
            if (input[startAt].toInt() == 0) {
                ++startAt
            }
            temp[--j] = ALPHABET[mod.toInt()].toByte()
        }

        // Strip extra '1' if there are some after decoding.
        while (j < temp.size && temp[j] == ALPHABET[0].toByte()) {
            ++j
        }
        // Add as many leading '1' as there were leading zeros.
        while (--zeroCount >= 0) {
            temp[--j] = ALPHABET[0].toByte()
        }

        val output = copyOfRange(temp, j, temp.size)
        try {
            return String(output)
        } catch (e: UnsupportedEncodingException) {
            throw RuntimeException(e)  // Cannot happen.
        }

    }

    //
    // number -> number / 58, returns number % 58
    //
    private fun divmod58(number: ByteArray, startAt: Int): Byte {
        var remainder = 0
        for (i in startAt..number.size - 1) {
            val digit256 = number[i].toInt() and 0xFF
            val temp = remainder * 256 + digit256

            number[i] = (temp / 58).toByte()

            remainder = temp % 58
        }

        return remainder.toByte()
    }


    private fun copyOfRange(source: ByteArray, from: Int, to: Int): ByteArray {
        val range = ByteArray(to - from)
        System.arraycopy(source, from, range, 0, range.size)

        return range
    }
}
