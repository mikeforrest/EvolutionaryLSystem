//=============================================================================
// Copyright 2006-2010 Daniel W. Dyer
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
package org.uncommons.watchmaker.examples.biomorphs;

import java.util.Arrays;

/**
 * @author Michael Forrest
 */
public final class Biomorph {
    public String axiom;
    public String[] ruleList;
    public int turnAngle;

    /*Creates a new Biomorph with an axiom (for example "ffg"),
       a rule list (for example "f=f+f-g","g=gg"),
       and a turn angle (any int 1-25)
     */
    public Biomorph(String axiom, String[] ruleList, int turnAngle) {
        this.axiom = axiom;
        this.ruleList = ruleList.clone();
        this.turnAngle = turnAngle;
    }


    public String getAxiom() {
        return axiom;
    }

    public String[] getRuleList() {
        return ruleList.clone();
    }

    public int getTurnAngle() {
        return turnAngle;
    }

}