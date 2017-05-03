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

import java.util.*;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JComponent;
import org.uncommons.watchmaker.framework.interactive.Renderer;
import java.awt.Color;



/**
 * Renders Biomorphs as Swing components.
 * @author Daniel Dyer
 * Modified by Michael Forrest
 */
public class SwingBiomorphRenderer implements Renderer<Biomorph, JComponent>
{
    /**
     * Renders an evolved biomorph as a component that can be displayed
     * in a Swing GUI.
     * @param biomorph The biomorph to render.
     * @return A component that displays a visual representation of the
     * biomorph.
     */
    public JComponent render(Biomorph biomorph)
    {
        return new BiomorphView(biomorph);
    }


    /**
     * A Swing component that can display a visual representation of a
     * biomorph.
     */
    private static final class BiomorphView extends JComponent
    {
        private static final int DRAW_WIDTH = 200;
        private static final int DRAW_HEIGHT = 200;

        private Color color_K = Color.BLACK;
        private Color color_R = new Color(200, 0, 0);  //Deep Red
        private Color color_G = new Color(143, 188, 139);  //Sand Green
        private Color color_B = new Color(147, 112, 219); //Medium Purple
        private Color color_C = new Color(15, 82, 186); //Sapphire
        private Color color_O = new Color(255, 117, 24);//Pumpkin;

        private final Biomorph biomorph;

        BiomorphView(Biomorph biomorph)
        {
            this.biomorph = biomorph;
            Dimension size = new Dimension(200, 200);
            setMinimumSize(size);
            setPreferredSize(size);
        }


        @Override
        protected void paintComponent(Graphics graphics)
        {
            super.paintComponent(graphics);
            if (graphics instanceof Graphics2D)
            {
                ((Graphics2D) graphics).setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                                         RenderingHints.VALUE_ANTIALIAS_ON);
            }

            String axiom = biomorph.getAxiom();
            String[] ruleList = biomorph.getRuleList();
            int turnAngle = biomorph.getTurnAngle();

            drawLSystem(graphics, axiom, ruleList, turnAngle);
        }


        /**
         * Recursive method for drawing tree branches.
         */
        private void drawLSystem(Graphics graphics, String axiom, String[] ruleList, int turnAngle){
            int generations = 5;

            setCustomColors();
            String cmdStr = axiom;

            for (int n = 1; n<=generations; n++)
            {
                cmdStr = buildString(cmdStr, ruleList, 1);
                drawCmd(graphics, turnAngle, cmdStr);
                System.out.println("Generation " + n + ":" + cmdStr);
                try { Thread.sleep(1000);}
                catch (InterruptedException e) {}

            }
        }


        public static String buildString(String axiom, String[] rules, int generation)
        {
            String currentStr = axiom;
            int ruleToFollow = 0;
            int numberOfRulesToFollow;
            int numberOfRules = rules.length;
            Random rand = new Random();

            for (int n = 1; n<=generation; n++)
            {

                String nextStr = "";
                for (int i=0; i<currentStr.length(); i++)
                {
                    char c = currentStr.charAt(i);

                    boolean isTerminal = true;


                    numberOfRulesToFollow = rand.nextInt(numberOfRules + 1);
                    //numberOfRulesToFollow = 1;

                    for (int counter = 0; counter<numberOfRulesToFollow; counter++)
                        ruleToFollow = rand.nextInt(numberOfRules);

                    { if (c == rules[ruleToFollow].charAt(0))
                    { nextStr += rules[ruleToFollow].substring(2);
                        isTerminal = false;
                    }
                    }
                    if (isTerminal) nextStr += c;
                }
                currentStr = nextStr;
            }


            return currentStr;
        }


        private void drawCmd(Graphics canvas, double turnAngle, String cmd)
        {
            System.out.println("draw");
            double drawLength = 1.8;
            canvas.setColor(Color.WHITE);
            canvas.fillRect(0, 0, DRAW_WIDTH, DRAW_HEIGHT);

            //set default turtle state
            double turtleX = DRAW_WIDTH/2;
            double turtleY = DRAW_HEIGHT/2;
            double turtleHeading = -190.3;

            int openBrackets = 0;
            ArrayList<Double> turtleBracketLocsX = new ArrayList<Double>();
            ArrayList<Double> turtleBracketLocsY = new ArrayList<Double>();

            canvas.setColor(Color.BLACK);
            turnAngle = Math.toRadians(turnAngle);

            System.out.println(cmd);


            //Walk through each character in command string and apply command.
            for (int i= 0; i<cmd.length(); i++)
            {
                char c=cmd.charAt(i);
                if (c == 'f' || c == 'h' || c == 'g')
                {
                    double nextX = turtleX + drawLength*Math.cos(turtleHeading);
                    double nextY = turtleY + drawLength*Math.sin(turtleHeading);


                    if (c != 'g')
                    { canvas.drawLine((int)turtleX, (int)turtleY, (int)nextX, (int)nextY);
                    }
                    turtleX = nextX;
                    turtleY = nextY;
                }

                else if (c == '+') turtleHeading += turnAngle;
                else if (c == '-') turtleHeading -= turnAngle;
                else if (c == '['){
                    turtleBracketLocsX.add(turtleX);
                    turtleBracketLocsY.add(turtleY);
                    openBrackets += 1;
    	  /*System.out.println(openBrackets+"[");
    	  System.out.println(turtleBracketLocsX);
    	  System.out.println(turtleBracketLocsY);*/

                }
                else if (c == ']'){
                    if (openBrackets > 0) {
                        turtleX = turtleBracketLocsX.get(openBrackets - 1);
                        turtleY = turtleBracketLocsY.get(openBrackets - 1);
                        turtleBracketLocsX.remove(openBrackets - 1);
                        turtleBracketLocsY.remove(openBrackets - 1);
                        openBrackets -= 1;
                    }
    	  /*System.out.println(openBrackets+"]");

    	  System.out.println(turtleBracketLocsX);
    	  System.out.println(turtleBracketLocsY);*/

                }
                else if (c == 'K') canvas.setColor(color_K);
                else if (c == 'R') canvas.setColor(color_R);
                else if (c == 'G') canvas.setColor(color_G);
                else if (c == 'B') canvas.setColor(color_B);
                else if (c == 'C') canvas.setColor(color_C);
                else if (c == 'O') canvas.setColor(color_O);
            }

        }


        private static String removeWhitespace(String str)
        { String str2 = "";
            for (int i=0; i<str.length(); i++)
            {
                char c = str.charAt(i);
                if (c != ' ' && c != '\t') str2 += c;
            }
            return str2;
        }

        private void setCustomColors()
        {


            String cmd = "R";

            char c = cmd.charAt(0);
            Color curColor = color_R;

        }
    }
}
