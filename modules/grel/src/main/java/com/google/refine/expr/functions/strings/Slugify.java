/*

Copyright 2024, OpenRefine contributors
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are
met:

    * Redistributions of source code must retain the above copyright
notice, this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above
copyright notice, this list of conditions and the following disclaimer
in the documentation and/or other materials provided with the
distribution.
    * Neither the name of Google Inc. nor the names of its
contributors may be used to endorse or promote products derived from
this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
"AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

*/

package com.google.refine.expr.functions.strings;

import java.text.Normalizer;
import java.util.Locale;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.refine.expr.EvalError;
import com.google.refine.grel.ControlFunctionRegistry;
import com.google.refine.grel.EvalErrorMessage;
import com.google.refine.grel.Function;
import com.google.refine.grel.FunctionDescription;

public class Slugify implements Function {

    // Combining diacritical marks left over after NFKD normalization (reused idea from Normalize).
    static final Pattern DIACRITICS_AND_FRIENDS = Pattern
            .compile("[\\p{InCombiningDiacriticalMarks}\\p{IsLm}\\p{IsSk}]+");

    // Any run of characters that is not an ASCII letter or digit.
    static final Pattern NON_ALPHANUMERIC = Pattern.compile("[^a-z0-9]+");

    @Override
    public Object call(Properties bindings, Object[] args) {
        if (args.length >= 1 && args.length <= 2 && args[0] != null) {
            String separator = "-";
            if (args.length == 2) {
                if (args[1] == null) {
                    return new EvalError(EvalErrorMessage.expects_one_or_two_strings(ControlFunctionRegistry.getFunctionName(this)));
                }
                separator = args[1].toString();
            }
            return slugify(args[0].toString(), separator);
        }
        return new EvalError(EvalErrorMessage.expects_one_or_two_strings(ControlFunctionRegistry.getFunctionName(this)));
    }

    private String slugify(String s, String separator) {
        s = Normalizer.normalize(s, Normalizer.Form.NFKD);
        s = DIACRITICS_AND_FRIENDS.matcher(s).replaceAll("");
        s = s.toLowerCase(Locale.ROOT);
        s = NON_ALPHANUMERIC.matcher(s).replaceAll(Matcher.quoteReplacement(separator));

        // Trim leading and trailing separators so the slug never starts or ends with one.
        if (!separator.isEmpty()) {
            while (s.startsWith(separator)) {
                s = s.substring(separator.length());
            }
            while (s.endsWith(separator)) {
                s = s.substring(0, s.length() - separator.length());
            }
        }
        return s;
    }

    @Override
    public String getDescription() {
        return FunctionDescription.str_slugify();
    }

    @Override
    public String getParams() {
        return "string s, optional string separator";
    }

    @Override
    public String getReturns() {
        return "string";
    }
}
