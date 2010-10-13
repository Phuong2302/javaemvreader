/*
 * Copyright 2010 sasc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package sasc;

import java.io.PrintWriter;
import java.io.StringWriter;
import sasc.emv.AID;
import sasc.emv.Application;
import sasc.emv.CA;
import sasc.emv.EMVCard;
import sasc.emv.EMVException;
import sasc.emv.EMVSession;
import sasc.emv.Log;
import sasc.emv.SessionProcessingEnv;
import sasc.terminal.CardConnection;
import sasc.terminal.TerminalException;

/**
 *
 * @author sasc
 */
public class CardEmulatorMain {

    public static void main(String[] args) throws TerminalException{
        EMVCard emvCard = null;
        try {
            CA.initFromFile("/CertificationAuthorities_Test.xml");
            CardConnection conn = new CardEmulator("/SDACardTransaction.xml");
            EMVSession session = conn.startSession(new SessionProcessingEnv());

            AID targetAID = new AID("a1 23 45 67 89 10 10"); //Our TEST AID

            emvCard = session.initCard();
            for (Application app : emvCard.getApplications()) {
                session.selectApplication(app);
                session.initiateApplicationProcessing();
                session.readApplicationData();
                session.readAdditionalData();
                if (targetAID.equals(app.getAID())) {
                    session.verifyPIN(1234, true);
                }
//            session.getChallenge(app);
            }
            Log.info("Finished Processing card.");
            Log.info("Now dumping card data in a more readable form:");
            Log.info("\n");
        } catch (TerminalException ex) {
            throw ex;
        } catch (EMVException ex) {
            throw ex;
        } finally {
            if (emvCard != null) {
                StringWriter dumpWriter = new StringWriter();
                emvCard.dump(new PrintWriter(dumpWriter), 0);
                System.out.println(dumpWriter.toString());
            }
        }
    }
}
