// ============================================================================
//
// Copyright (C) 2006-2014 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend;

import org.junit.Assert;
import org.junit.Test;

public class StringsUtilsTest {

    @Test
    public void testRemoveLastCharsString() {
        Assert.assertEquals("taga", StringsUtils.removeLastChars("tagada", 2));
    }

    @Test
    public void testRemoveLastCharsStringBuilder() {
        StringBuilder sb = new StringBuilder();
        sb.append("ta");
        sb.append("ga");
        sb.append("da");

        StringsUtils.removeLastChars(sb, 2);
        Assert.assertEquals("taga", sb.toString());
    }
}