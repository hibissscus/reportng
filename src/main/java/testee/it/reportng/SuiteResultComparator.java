//=============================================================================
// Copyright 2006-2013 Daniel W. Dyer
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//=============================================================================
package testee.it.reportng;

import org.testng.ISuiteResult;

import java.util.Comparator;

/**
 * Comparator for sorting TestNG test results by passRate and alphabetically by method name.
 */
class SuiteResultComparator implements Comparator<ISuiteResult> {
    public int compare(ISuiteResult result1, ISuiteResult result2) {
        final double rate1 = rate(result1);
        final double rate2 = rate(result2);
        if (rate1 == rate2) {
            return result1.getTestContext().getName().compareTo(result2.getTestContext().getName());
        }
        return rate1 > rate2 ? 1 : -1;
    }

    private double rate(final ISuiteResult result) {
        if (result.getTestContext().getPassedTests().size() <= 0) {
            return 0;
        } else {
            final double passedTests = result.getTestContext().getPassedTests().size();
            final double failedTests = result.getTestContext().getFailedTests().size();
            final double skippedTests = result.getTestContext().getSkippedTests().size();
            return passedTests / (passedTests + failedTests + skippedTests) * 100;
        }
    }

}
