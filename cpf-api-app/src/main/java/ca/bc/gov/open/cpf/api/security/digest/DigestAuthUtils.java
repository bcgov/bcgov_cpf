/*
 * Copyright © 2008-2015, Province of British Columbia
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ca.bc.gov.open.cpf.api.security.digest;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.security.core.codec.Hex;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

final class DigestAuthUtils {

  static String encodePasswordInA1Format(final String username,
    final String realm, final String password) {
    final String a1 = username + ":" + realm + ":" + password;
    final String a1Md5 = md5Hex(a1);

    return a1Md5;
  }

  /**
   * Computes the <code>response</code> portion of a Digest authentication header. Both the server and user
   * agent should compute the <code>response</code> independently. Provided as a static method to simplify the
   * coding of user agents.
   *
   * @param passwordAlreadyEncoded true if the password argument is already encoded in the correct format. False if
   *                               it is plain text.
   * @param username               the user's login name.
   * @param realm                  the name of the realm.
   * @param password               the user's password in plaintext or ready-encoded.
   * @param httpMethod             the HTTP request method (GET, POST etc.)
   * @param uri                    the request URI.
   * @param qop                    the qop directive, or null if not set.
   * @param nonce                  the nonce supplied by the server
   * @param nc                     the "nonce-count" as defined in RFC 2617.
   * @param cnonce                 opaque string supplied by the client when qop is set.
   * @return the MD5 of the digest authentication response, encoded in hex
   * @throws IllegalArgumentException if the supplied qop value is unsupported.
   */
  static String generateDigest(final boolean passwordAlreadyEncoded,
    final String username, final String realm, final String password,
    final String httpMethod, final String uri, final String qop,
    final String nonce, final String nc, final String cnonce)
    throws IllegalArgumentException {
    String a1Md5 = null;
    final String a2 = httpMethod + ":" + uri;
    final String a2Md5 = md5Hex(a2);

    if (passwordAlreadyEncoded) {
      a1Md5 = password;
    } else {
      a1Md5 = DigestAuthUtils.encodePasswordInA1Format(username, realm,
        password);
    }

    String digest;

    if (qop == null) {
      // as per RFC 2069 compliant clients (also reaffirmed by RFC 2617)
      digest = a1Md5 + ":" + nonce + ":" + a2Md5;
    } else if ("auth".equals(qop)) {
      // As per RFC 2617 compliant clients
      digest = a1Md5 + ":" + nonce + ":" + nc + ":" + cnonce + ":" + qop + ":"
        + a2Md5;
    } else {
      throw new IllegalArgumentException(
        "This method does not support a qop: '" + qop + "'");
    }

    final String digestMd5 = new String(md5Hex(digest));

    return digestMd5;
  }

  static String md5Hex(final String data) {
    MessageDigest digest;
    try {
      digest = MessageDigest.getInstance("MD5");
    } catch (final NoSuchAlgorithmException e) {
      throw new IllegalStateException("No MD5 algorithm available!");
    }

    return new String(Hex.encode(digest.digest(data.getBytes())));
  }

  /**
   * Splits a <code>String</code> at the first instance of the delimiter.<p>Does not include the delimiter in
   * the response.</p>
   *
   * @param toSplit   the string to split
   * @param delimiter to split the string up with
   * @return a two element array with index 0 being before the delimiter, and index 1 being after the delimiter
   *         (neither element includes the delimiter)
   * @throws IllegalArgumentException if an argument was invalid
   */
  static String[] split(final String toSplit, final String delimiter) {
    Assert.hasLength(toSplit, "Cannot split a null or empty string");
    Assert.hasLength(delimiter,
      "Cannot use a null or empty delimiter to split a string");

    if (delimiter.length() != 1) {
      throw new IllegalArgumentException(
        "Delimiter can only be one character in length");
    }

    final int offset = toSplit.indexOf(delimiter);

    if (offset < 0) {
      return null;
    }

    final String beforeDelimiter = toSplit.substring(0, offset);
    final String afterDelimiter = toSplit.substring(offset + 1);

    return new String[] {
      beforeDelimiter, afterDelimiter
    };
  }

  /**
   * Takes an array of <code>String</code>s, and for each element removes any instances of
   * <code>removeCharacter</code>, and splits the element based on the <code>delimiter</code>. A <code>Map</code> is
   * then generated, with the left of the delimiter providing the key, and the right of the delimiter providing the
   * value.<p>Will trim both the key and value before adding to the <code>Map</code>.</p>
   *
   * @param array            the array to process
   * @param delimiter        to split each element using (typically the equals symbol)
   * @param removeCharacters one or more characters to remove from each element prior to attempting the split
   *                         operation (typically the quotation mark symbol) or <code>null</code> if no removal should occur
   * @return a <code>Map</code> representing the array contents, or <code>null</code> if the array to process was
   *         null or empty
   */
  static Map<String, String> splitEachArrayElementAndCreateMap(
    final String[] array, final String delimiter, final String removeCharacters) {
    if (array == null || array.length == 0) {
      return null;
    }

    final Map<String, String> map = new HashMap<String, String>();

    for (int i = 0; i < array.length; i++) {
      String postRemove;

      if (removeCharacters == null) {
        postRemove = array[i];
      } else {
        postRemove = StringUtils.replace(array[i], removeCharacters, "");
      }

      final String[] splitThisArrayElement = split(postRemove, delimiter);

      if (splitThisArrayElement == null) {
        continue;
      }

      map.put(splitThisArrayElement[0].trim(), splitThisArrayElement[1].trim());
    }

    return map;
  }

  static String[] splitIgnoringQuotes(final String str, final char separatorChar) {
    if (str == null) {
      return null;
    }

    final int len = str.length();

    if (len == 0) {
      return EMPTY_STRING_ARRAY;
    }

    final List<String> list = new ArrayList<String>();
    int i = 0;
    int start = 0;
    boolean match = false;

    while (i < len) {
      if (str.charAt(i) == '"') {
        i++;
        while (i < len) {
          if (str.charAt(i) == '"') {
            i++;
            break;
          }
          i++;
        }
        match = true;
        continue;
      }
      if (str.charAt(i) == separatorChar) {
        if (match) {
          list.add(str.substring(start, i));
          match = false;
        }
        start = ++i;
        continue;
      }
      match = true;
      i++;
    }
    if (match) {
      list.add(str.substring(start, i));
    }

    return list.toArray(new String[list.size()]);
  }

  private static final String[] EMPTY_STRING_ARRAY = new String[0];
}
