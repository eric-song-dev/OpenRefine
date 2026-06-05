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

import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.google.refine.expr.EvalError;
import com.google.refine.grel.GrelTestBase;

public class SlugifyTests extends GrelTestBase {

    @Override
    @BeforeTest
    public void init() {
        logger = LoggerFactory.getLogger(this.getClass());
    }

    @Test
    public void testBasicSlugify() {
        Assert.assertEquals(invoke("slugify", "Hello, World! 2024"), "hello-world-2024");
    }

    @Test
    public void testSlugifyStripsDiacritics() {
        Assert.assertEquals(invoke("slugify", "Café Münchìn"), "cafe-munchin");
    }

    @Test
    public void testSlugifyTrimsLeadingAndTrailingSeparators() {
        Assert.assertEquals(invoke("slugify", "  Multiple   Spaces  "), "multiple-spaces");
        Assert.assertEquals(invoke("slugify", "***Wrapped***"), "wrapped");
    }

    @Test
    public void testSlugifyCustomSeparator() {
        Assert.assertEquals(invoke("slugify", "Hello World", "_"), "hello_world");
    }

    @Test
    public void testSlugifyEmptyString() {
        Assert.assertEquals(invoke("slugify", ""), "");
    }

    @Test
    public void testSlugifyOnlyPunctuation() {
        Assert.assertEquals(invoke("slugify", "!!!---???"), "");
    }

    @Test
    public void testSlugifyNullValue() {
        Assert.assertTrue(invoke("slugify", (Object) null) instanceof EvalError);
    }

    @Test
    public void testSlugifyNoArgs() {
        Assert.assertTrue(invoke("slugify") instanceof EvalError);
    }

    @Test
    public void testSlugifyTooManyArgs() {
        Assert.assertTrue(invoke("slugify", "a", "b", "c") instanceof EvalError);
    }
}
