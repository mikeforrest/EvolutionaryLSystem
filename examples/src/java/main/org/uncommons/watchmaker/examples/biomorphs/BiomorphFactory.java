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

import java.util.Random;
import org.uncommons.watchmaker.framework.factories.AbstractCandidateFactory;

/**
 * Candidate factory for creating random biomorphs.
 * @author Daniel Dyer
 * Modified by Michael Forrest
 */
public class BiomorphFactory extends AbstractCandidateFactory<Biomorph> {
    /**
     * Generates a random biomorph by providing a random value for each gene.
     *
     * @param rng The source of randomness used to generate the biomoprh.
     * @return A randomly-generated biomorph.
     */

    private final String[] fSubComponents = new String[]{"f", "g", "h", "+", "-"}; //random combinations of these components will make up the axiom as well as both sides of each rule.

/*
Creates a new biomorph using the values genereated by the three generation functions.
 */
    public Biomorph generateRandomCandidate(Random rng) {
        String axiom = generateAxiom();
        String[] ruleList = generateRuleList();
        int turnAngle = generateTurnAngle();
        return new Biomorph(axiom, ruleList, turnAngle);
    }

    public String generateAxiom() {
        Random rng = new Random();
        int axiomSize = rng.nextInt(4) + 1; //axioms are 1-5 characters long
        String axiom = ""; //start with a null axiom
        for (int i = 0; i < axiomSize; i++) {
            axiom += fSubComponents[rng.nextInt(3)]; //add a random fSubComponent to the axiom until axiom size is reached
        }
        //System.out.println("Axiom:" + axiom);
        return axiom;
    }

    public int generateTurnAngle() {
        Random rng = new Random();
        int turnAngle = rng.nextInt(20) + 4; //turn angles can be 4-24 degrees to create realistic branching patterns
        return turnAngle;
    }

    public String[] generateFComponents() { //generates the small substrings ("f+f+f" or "ff--g+h" for example) that are combined with bracket prefabs to create random rules.
        Random rng = new Random();
        String[] fComponents = new String[100]; //100 unique components gives a high range of generative posibilities
        for (int i = 0; i < 99; i++){ //for each element in the array, generate a component and set that element equal to that component.
            int compSize = rng.nextInt(5) + 1; //each component is 1-6 characters long
            String comp = ""; //start with a null component
            for (int j = 0; j < compSize; j ++){ //add a semi-random character until the desired component size is reached;
                 int letterOrSymbol = rng.nextInt(100) + 1; //used to make letters more likely than + or - symbols
                if (letterOrSymbol > 30) { //70% chance of letters, 30% chance of + or -
                    comp += fSubComponents[rng.nextInt(3)]; // add f, g or h
                }
                else{
                    boolean plusOrMinus = rng.nextBoolean(); //used to randomly determine if plus or minus is used; true = + | false = -
                    if (plusOrMinus == true){
                        comp += "+";
                    }
                    else{
                        comp += "-";
                    }
                }
            }
            fComponents[i] = comp;
        }
        return fComponents;
    }

    public String[] generateBComponents() { //combines randomly generated fcomponents with bracket prefabs such as "[]" or "[[[]]]" to create the final components that can be randomized to generate rules
        Random rng = new Random();
        String[] fComponents = generateFComponents();
        String[] bComponents = new String[]{fComponents[rng.nextInt(100)], //a single random fcomponent
                "[" + fComponents[rng.nextInt(100)] + "]" + fComponents[rng.nextInt(100)], // [X][X] where X is equal to random fcomponent
                fComponents[rng.nextInt(100)] + "[" + fComponents[rng.nextInt(100)], // X[X]
                "[[" + fComponents[rng.nextInt(100)] + "][" + fComponents[rng.nextInt(100)] + "]]", // [[X]+X]]
                "[" + fComponents[rng.nextInt(100)] + "[" + fComponents[rng.nextInt(100)] + "][" + fComponents[rng.nextInt(100)] + "]]", // [X[X][X]]
                "[[" + fComponents[rng.nextInt(100)] + "]" + fComponents[rng.nextInt(100)] + "[" + fComponents[rng.nextInt(100)] + "]]", // [X]X[X]]
                "[[" + fComponents[rng.nextInt(100)] + "][" + fComponents[rng.nextInt(100)] + "]" + fComponents[rng.nextInt(100)] + "]", // [[X][X]X]
                "[" + fComponents[rng.nextInt(100)] + "[" + fComponents[rng.nextInt(100)] + "]" + fComponents[rng.nextInt(100)] + "[" + fComponents[rng.nextInt(100)] + "]]", // [X[X]X[X]]
                "[" + fComponents[rng.nextInt(100)] + "[" + fComponents[rng.nextInt(100)] + "]" + fComponents[rng.nextInt(100)] + "[" + fComponents[rng.nextInt(100)] + "]" + fComponents[rng.nextInt(100)] + "]", // [X[X]X[X]X]
                "[" + fComponents[rng.nextInt(100)] + "[" + fComponents[rng.nextInt(100)] + "[" + fComponents[rng.nextInt(100)] + "]" + fComponents[rng.nextInt(100)] + "]" + fComponents[rng.nextInt(100)] + "]", // [X[X[X]X]X]
                "[" + fComponents[rng.nextInt(100)] + "[" + fComponents[rng.nextInt(100)] + "[" + fComponents[rng.nextInt(100)] + "]]]", // [X[X[X]]]
                "[[[" + fComponents[rng.nextInt(100)] + "]" + fComponents[rng.nextInt(100)] + "]" + fComponents[rng.nextInt(100)] + "]"}; // [[[X]X]X]
    return bComponents;
    }

    public String[] generateRuleList(){
        Random rng = new Random();
        int ruleListSize;
        ruleListSize = rng.nextInt(3) + 1; //rule lists are 1-4 rules long
        String[] ruleList = new String[ruleListSize];
        String[] bComponents = generateBComponents();

        for (int i = 0; i < ruleListSize; i++){ //generate a rule for each element in ruleList
            int ruleSize = rng.nextInt (3) + 1;
            boolean stringNull = true;
            for (int j = 0; j < (ruleSize); j++) {
                 if (stringNull){
                     ruleList[i] = (fSubComponents[rng.nextInt(3)]) + "="; //generates the left side of the rule, for example "f=" or "g="
                     ruleList[i] = ruleList[i] + bComponents[(rng.nextInt(10) + 1)]; //generates the ride side of the rule using a single random bcomponent
                     stringNull = false;
                 }
                 else {
                     ruleList[i] = ruleList[i] + bComponents[(rng.nextInt(10) + 1)]; //adds a single random bcomponent to the end of the existing rule
                 }

            }
        }
        return ruleList;
    }
}

