package abstraction.eq5TRAN;

import static abstraction.eq5TRAN.util.Marchandises.FEVES_BQ;
import static abstraction.eq5TRAN.util.Marchandises.FEVES_MQ;
import static abstraction.eq5TRAN.util.Marchandises.FRIANDISES_MQ;
import static abstraction.eq5TRAN.util.Marchandises.POUDRE_HQ;
import static abstraction.eq5TRAN.util.Marchandises.POUDRE_MQ;
import static abstraction.eq5TRAN.util.Marchandises.TABLETTES_BQ;
import static abstraction.eq5TRAN.util.Marchandises.TABLETTES_HQ;
import static abstraction.eq5TRAN.util.Marchandises.TABLETTES_MQ;

import java.util.*;

import abstraction.eq2PROD.Eq2PROD;
import abstraction.eq3PROD.Eq3PROD;
import abstraction.eq3PROD.echangesProdTransfo.ContratFeve;
import abstraction.eq3PROD.echangesProdTransfo.IAcheteurFeve;
import abstraction.eq3PROD.echangesProdTransfo.IVendeurFeve;
import abstraction.eq5TRAN.appeldOffre.DemandeAO;
import abstraction.eq5TRAN.appeldOffre.IvendeurOccasionnelChoco;
import abstraction.eq5TRAN.util.Marchandises;
import abstraction.eq5TRAN.util.ValueComparator;
import abstraction.eq7TRAN.echangeTRANTRAN.ContratPoudre;
import abstraction.eq7TRAN.echangeTRANTRAN.IAcheteurPoudre;
import abstraction.eq7TRAN.echangeTRANTRAN.IVendeurPoudre;
import abstraction.fourni.Acteur;
import abstraction.fourni.Indicateur;
import abstraction.fourni.Journal;
import abstraction.fourni.Monde;

/**
 * @author Juliette Gorline (chef)
 * @author Francois Le Guernic
 * @author Maxim Poulsen
 * @author Thomas Schillaci (lieutenant)
 */
public class Eq5TRAN implements Acteur, IAcheteurPoudre, IVendeurPoudre, IvendeurOccasionnelChoco, IAcheteurFeve {

    // cf Marchandises.java pour obtenir l'indexation
    private Indicateur[] productionSouhaitee; // ce qui sort de nos machines en kT
    private Indicateur[] achatsSouhaites; // ce qu'on achète aux producteurs en kT
    private float facteurStock; // facteur lié aux risques= combien d'itérations on peut tenir sans réception de feves/poudre
    private Indicateur[] stocksSouhaites; // margeStock = facteurStock * variationDeStockParIteration, en kT
    private Indicateur[] stocks; // les vrais stocks en kT
    private ContratFeve[] offrePublique;

    private Indicateur banque; // en milliers d'euros
    private Indicateur[] prix; // en €/T

    private final int FEVE_BQ_EQ2 = 0;
    private final int FEVE_MQ_EQ2 = 1;
    private final int FEVE_MQ_EQ3 = 2;

    private Journal journal;

    /**
     * @author Thomas Schillaci
     */
    public Eq5TRAN() {
        int nbMarchandises = Marchandises.getNombreMarchandises();
        productionSouhaitee = new Indicateur[nbMarchandises];
        achatsSouhaites = new Indicateur[nbMarchandises];
        facteurStock = 3;
        stocksSouhaites = new Indicateur[nbMarchandises];
        stocks = new Indicateur[nbMarchandises];
        prix = new Indicateur[nbMarchandises];

        productionSouhaitee[FEVES_BQ] = new Indicateur("Eq5 - Production souhaitee de feves BQ", this, 0);
        productionSouhaitee[FEVES_MQ] = new Indicateur("Eq5 - Production souhaitee de feves MQ", this, 0);
        productionSouhaitee[TABLETTES_BQ] = new Indicateur("Eq5 - Production souhaitee de tablettes BQ", this, 345);
        productionSouhaitee[TABLETTES_MQ] = new Indicateur("Eq5 - Production souhaitee de tablettes MQ", this, 575);
        productionSouhaitee[TABLETTES_HQ] = new Indicateur("Eq5 - Production souhaitee de tablettes HQ", this, 115);
        productionSouhaitee[POUDRE_MQ] = new Indicateur("Eq5 - Production souhaitee de poudre MQ", this, 50);
        productionSouhaitee[POUDRE_HQ] = new Indicateur("Eq5 - Production souhaitee de poudre HQ", this, 0);
        productionSouhaitee[FRIANDISES_MQ] = new Indicateur("Eq5 - Production souhaitee de friandises MQ", this, 115);

        achatsSouhaites[FEVES_BQ] = new Indicateur("Eq5 - Achats souhaites de feves BQ", this, 360);
        achatsSouhaites[FEVES_MQ] = new Indicateur("Eq5 - Achats souhaites de feves MQ", this, 840);
        achatsSouhaites[TABLETTES_BQ] = new Indicateur("Eq5 - Achats souhaites de tablettes BQ", this, 0);
        achatsSouhaites[TABLETTES_MQ] = new Indicateur("Eq5 - Achats souhaites de tablettes MQ", this, 0);
        achatsSouhaites[TABLETTES_HQ] = new Indicateur("Eq5 - Achats souhaites de tablettes HQ", this, 0);
        achatsSouhaites[POUDRE_MQ] = new Indicateur("Eq5 - Achats souhaites de poudre MQ", this, 0);
        achatsSouhaites[POUDRE_HQ] = new Indicateur("Eq5 - Achats souhaites de poudre HQ", this, 0);
        achatsSouhaites[FRIANDISES_MQ] = new Indicateur("Eq5 - Achats souhaites de friandises MQ", this, 0);

        prix[FEVES_BQ] = new Indicateur("Eq5 - Prix de feves BQ", this, 0);
        prix[FEVES_MQ] = new Indicateur("Eq5 - Prix de feves MQ", this, 0);
        prix[TABLETTES_BQ] = new Indicateur("Eq5 - Prix de tablettes BQ", this, 100);
        prix[TABLETTES_MQ] = new Indicateur("Eq5 - Prix de tablettes MQ", this, 100);
        prix[TABLETTES_HQ] = new Indicateur("Eq5 - Prix de tablettes HQ", this, 0);
        prix[POUDRE_MQ] = new Indicateur("Eq5 - Prix de poudre MQ", this, 100);
        prix[POUDRE_HQ] = new Indicateur("Eq5 - Prix de poudre HQ", this, 0);
        prix[FRIANDISES_MQ] = new Indicateur("Eq5 - Prix de friandises MQ", this, 100);

        for (int i = 0; i < nbMarchandises; i++) {
            stocksSouhaites[i] = new Indicateur("Eq5 - Stocks souhaites de " + Marchandises.getMarchandise(i), this, productionSouhaitee[i].getValeur() + achatsSouhaites[i].getValeur());
            stocks[i] = new Indicateur("Eq5 - Stocks de " + Marchandises.getMarchandise(i), this, stocksSouhaites[i].getValeur()); // on initialise les vrais stocks comme étant ce que l'on souhaite avoir pour la premiere iteration
        }

        banque = new Indicateur("Eq5 - Banque", this, 16_000); // environ benefice 2017 sur nombre d'usines

        //		for (Field field : getClass().getDeclaredFields()) {
        //			if(field==null) continue;
        //			try {
        //				if(field.get(this) instanceof  Indicateur)
        //					Monde.LE_MONDE.ajouterIndicateur((Indicateur) field.get(this));
        //				else if(field.get(this) instanceof Indicateur[])
        //					for (Indicateur indicateur : (Indicateur[]) field.get(this))
        //						Monde.LE_MONDE.ajouterIndicateur(indicateur);
        //			} catch (IllegalAccessException e) {
        //				e.printStackTrace();
        //			}
        //		}

        Monde.LE_MONDE.ajouterIndicateur(banque);
        Monde.LE_MONDE.ajouterIndicateur(stocks[TABLETTES_BQ]);
        Monde.LE_MONDE.ajouterIndicateur(stocks[TABLETTES_MQ]);
        Monde.LE_MONDE.ajouterIndicateur(stocks[TABLETTES_HQ]);

        journal = new Journal("Journal Eq5");
        Monde.LE_MONDE.ajouterJournal(journal);
    }

    @Override
    public String getNom() {
        return "Eq5TRAN";
    }

    @Override
    public void next() {
        achatAuxProducteurs();
    }

    /**
     * @author Thomas Schillaci
     */
    public void achatAuxProducteurs() {
        // Achats aux producteurs
        List<ContratFeve[]> contrats = new ArrayList<ContratFeve[]>();
        ArrayList<HashMap<IVendeurFeve, Double>> listePrix = new ArrayList<>();
        listePrix.add(new HashMap<IVendeurFeve,Double>()); // BQ
        listePrix.add(new HashMap<IVendeurFeve,Double>()); // MQ
        for(Acteur acteur : Monde.LE_MONDE.getActeurs()) {
            if(!(acteur instanceof IVendeurFeve)) continue;
            IVendeurFeve vendeur = (IVendeurFeve)acteur;
            ContratFeve[] contrat = vendeur.getOffrePublique();
            contrats.add(contrat);
            listePrix.get(0).put(vendeur,contrat[0].getOffrePublique_Prix());
            listePrix.get(1).put(vendeur,contrat[1].getOffrePublique_Prix());
        }

        // On trie les deux TreeMap par prix croissant
        TreeMap<IVendeurFeve, Double> prixBQ = new TreeMap<IVendeurFeve,Double>(new ValueComparator(listePrix.get(0)));
        TreeMap<IVendeurFeve, Double> prixMQ = new TreeMap<IVendeurFeve,Double>(new ValueComparator(listePrix.get(1)));
        ArrayList<TreeMap<IVendeurFeve,Double>> listePrixTriee = new ArrayList<TreeMap<IVendeurFeve,Double>>();
        listePrixTriee.add(prixBQ);
        listePrixTriee.add(prixMQ);

        // On établit les quantites demandees sachant qu'on essaie toujours d'acheter 70% au moins cher
        HashMap<IVendeurFeve, Integer[]> quantitesDemandees = new HashMap<IVendeurFeve,Integer[]>();
        for(IVendeurFeve vendeur : listePrixTriee.get(0).keySet()) quantitesDemandees.put(vendeur, new Integer[2]);
        for (int i = 0; i < 2; i++) { // Pour les feves BQ et MQ
            double resteACommander = achatsSouhaites[i].getValeur();

            boolean premier = true;
            for(IVendeurFeve vendeur : listePrixTriee.get(i).keySet()) {
                Integer quantite = (int)Math.min(resteACommander,listePrixTriee.get(i).get(vendeur));
                if(premier) {
                    premier=false;
                    quantite = (int)Math.min(0.7*resteACommander,listePrixTriee.get(i).get(vendeur));
                }
                resteACommander-=quantite;
                quantitesDemandees.get(vendeur)[i]+=quantite;
            }

            // si il reste a commander on se permet de depasser les 70%
            premier = true;
            for(IVendeurFeve vendeur : listePrixTriee.get(i).keySet()) {
                if(premier) {
                    premier=false;
                    Integer quantite = (int)Math.min(resteACommander,listePrixTriee.get(i).get(vendeur)-quantitesDemandees.get(vendeur)[i]);
                    resteACommander-=quantite;
                    quantitesDemandees.get(vendeur)[i]+=quantite;
                }
            }

            if(resteACommander>0) journal.ajouter("L'équipe 5 n'a pas réussi a commander assez de fèves " + (i==0?"BQ":"MQ") + ", manque de " + resteACommander + "T");
        }

        // On formule ensuite les demandes
        for(ContratFeve[] contrat : contrats) {
            for(IVendeurFeve vendeur : quantitesDemandees.keySet()) {
                if(!((Acteur)vendeur).getNom().equals(((Acteur)contrat[0].getProducteur()))) continue;
                Integer[] quantites = quantitesDemandees.get(vendeur);
                if(quantites[0]==0 && quantites[1]==0) continue;
                for (int i = 0; i < 2; i++) {
                    contrat[i].setDemande_Quantite(quantites[i]);
                    contrat[i].setDemande_Prix(quantites[i]*prix[i].getValeur());
                }
                vendeur.sendDemandePrivee(contrat);
            }
        }

        // On va maintenant etudier les propositions: version rudimentaire v1
        for(IVendeurFeve vendeur : quantitesDemandees.keySet()) {
            ContratFeve[] offreFinale = vendeur.getOffreFinale();
            int depense=0;
            for(ContratFeve contrat : offreFinale) {
                contrat.setReponse(true); // On accpete automatiquement les contrats issus des demandes que l'in avait formule
                depense+=contrat.getProposition_Quantite()*contrat.getProposition_Prix();
            }
            vendeur.sendResultVentes(offreFinale);
            depenser(depense);
            journal.ajouter("L'equipe 5 vient de passer une commande (officielle) au vendeur " + ((Acteur)vendeur).getNom() + " pour " + depense + "€");
        }
    }

    /**
     * @author Thomas Schillaci
     */
    public void depenser(double depense) {
        double resultat=banque.getValeur()-depense;
        banque.setValeur(this,resultat);
        if(resultat<0) journal.ajouter("L'equipe 5 est a decouvert !\nCompte en banque: "+banque.getValeur()+"€");
    }

    @Override
    /**
     * @author Juliette et Thomas
     */
    public ContratPoudre[] getCataloguePoudre(IAcheteurPoudre acheteur) {
        if (stocks[POUDRE_MQ].getValeur() == 0) return new ContratPoudre[0];

        ContratPoudre[] catalogue = new ContratPoudre[1];
        catalogue[0] = new ContratPoudre(1, (int) stocks[POUDRE_MQ].getValeur(), prix[POUDRE_MQ].getValeur(), acheteur, this, false);
        return catalogue;

    }

    @Override
    /**
     * @author Juliette
     * V1 : on n'envoie un devis que si la qualité demandée est moyenne (la seule que nous vendons) et que nous avons assez de stocks
     */
    public ContratPoudre[] getDevisPoudre(ContratPoudre[] demande, IAcheteurPoudre acheteur) {
        ContratPoudre[] devis = new ContratPoudre[demande.length];
        for (int i = 0; i < demande.length; i++) {
            if (demande[i].getQualite() != 1 && demande[i].getQuantite() < stocks[POUDRE_MQ].getValeur()) {
                devis[i] = new ContratPoudre(0, 0, 0, acheteur, this, false);
            } else {
                devis[i] = new ContratPoudre(demande[i].getQualite(), demande[i].getQuantite(), prix[POUDRE_MQ].getValeur(), acheteur, this, false);
            }
        }

        return devis;
    }

    @Override
    /**
     * @author Juliette
     * V1 : si la réponse est cohérente avec la demande initiale, nos stocks et nos prix, on répond oui
     */
    public void sendReponsePoudre(ContratPoudre[] devis, IAcheteurPoudre acheteur) {
        ContratPoudre[] reponse = new ContratPoudre[devis.length];
        for (int i = 0; i < devis.length; i++) {
            if (devis[i].getQualite() != 1 && devis[i].getQuantite() < stocks[POUDRE_MQ].getValeur() && devis[i].getPrix() == prix[POUDRE_MQ].getValeur()) {
                reponse[i] = new ContratPoudre(devis[i].getQualite(), devis[i].getQuantite(), devis[i].getPrix(), devis[i].getAcheteur(), devis[i].getVendeur(), true);
            } else {
                reponse[i] = new ContratPoudre(devis[i].getQualite(), devis[i].getQuantite(), devis[i].getPrix(), devis[i].getAcheteur(), devis[i].getVendeur(), false);
            }
        }
    }

    @Override
    /**
     * @author Juliette
     * Pour la V1 on suppose que le contrat est entièrement honnoré
     */
    public ContratPoudre[] getEchangeFinalPoudre(ContratPoudre[] contrat, IAcheteurPoudre acheteur) {
        ContratPoudre[] echangesEffectifs = new ContratPoudre[contrat.length];
        for (int i = 0; i < contrat.length; i++) {
            echangesEffectifs[i] = contrat[i];
        }
        return echangesEffectifs;
    }

    /**
     * @author Juliette
     * Dans cette méthode, nous sommes ACHETEURS
     * Methode permettant de récupérer les devis de poudre correspondant à nos demandes et de décider si on les accepte ou non
     */
    private void getTousLesDevisPoudre(ContratPoudre[] demande) {
        List<Acteur> listeActeurs = Monde.LE_MONDE.getActeurs();

        List<ContratPoudre[]> devis = new ArrayList<ContratPoudre[]>();

        for (Acteur acteur : listeActeurs) {
            if (acteur instanceof IVendeurPoudre) {
                devis.add(((IVendeurPoudre) acteur).getDevisPoudre(demande, this));
            }
        }
        for (ContratPoudre[] contrat : devis) {
            for (int j = 0; j < 3; j++) {
                if (contrat[j].equals(demande[j])) {
                    contrat[j].setReponse(true);
                }
            }
            contrat[0].getVendeur().sendReponsePoudre(contrat, this);
        }


    }

    /**
     * @author François Le Guernic
     */
    @Override
    public void sendOffrePublique(ContratFeve[] offrePublique) {
        /* On achète des fèves de BQ ( seulement à équipe 2 ) et de MQ ( à équipes 2 et 3 ) aux équipes de producteur
         *
         */

        ContratFeve[] c1 = new ContratFeve[3];

        // Pour récupérer les offres qui nous intéressent
        int i = 0;
        for (ContratFeve c : offrePublique) {
            if ((((Eq2PROD) c.getProducteur()).getNom() == "Eq2PROD" && c.getQualite() == 0)

                    || (((Eq2PROD) c.getProducteur()).getNom() == "Eq2PROD" && c.getQualite() == 1)
                    || (((Eq3PROD) c.getProducteur()).getNom() == "Eq3PROD" && c.getQualite() == 1)) {
                c1[i] = c;
                i++;
            }

        }

        this.offrePublique = new ContratFeve[3];
        for (int j = 0; j < 3; j++) {


            if (((Eq2PROD) c1[j].getProducteur()).getNom() == "Eq2PROD") {
                this.offrePublique[0] = c1[j];
            }
            ;
            if (((Eq2PROD) c1[j].getProducteur()).getNom() == "Eq2PROD" && c1[j].getQualite() == 1) {
                this.offrePublique[1] = c1[j];
            } else {
                this.offrePublique[2] = c1[j];
            }
        }
    }

    /**
     * @author Francois Le Guernic
     */
    @Override
    public ContratFeve[] getDemandePrivee() {

        /*Par convention, dans la liste de deux contrat, on aura dans l'ordre :
         * - le contrat pour les fèves BQ à l'équipe 2
         * - le contart pour les fèves MQ à l'équipe 2
         * - le contrat pour les fèves MQ à l'équipe 3
         */

        ContratFeve[] demandes = new ContratFeve[3];
        for (int i = 0; i < 3; i++) {
            demandes[i].setTransformateur(this);
            demandes[i].setReponse(false);
        }
        demandes[FEVE_BQ_EQ2].setProducteur((IVendeurFeve) Monde.LE_MONDE.getActeur("Eq2PROD"));
        demandes[FEVE_BQ_EQ2].setQualite(0);
        demandes[FEVE_BQ_EQ2].setDemande_Quantite((int) (this.achatsSouhaites[FEVES_BQ].getValeur()));
        demandes[FEVE_BQ_EQ2].setDemande_Prix(this.offrePublique[0].getPrix());
        demandes[FEVE_MQ_EQ2].setProducteur((IVendeurFeve) Monde.LE_MONDE.getActeur("Eq2PROD"));
        demandes[FEVE_MQ_EQ2].setQualite(1);
        demandes[FEVE_MQ_EQ2].setDemande_Quantite((int) (0.3 * this.achatsSouhaites[FEVES_MQ].getValeur()));
        demandes[FEVE_MQ_EQ2].setDemande_Prix(this.offrePublique[1].getPrix());
        demandes[FEVE_MQ_EQ3].setProducteur((IVendeurFeve) Monde.LE_MONDE.getActeur("Eq3PROD"));
        demandes[FEVE_MQ_EQ3].setQualite(1);
        demandes[FEVE_MQ_EQ3].setDemande_Quantite((int) (0.3 * this.achatsSouhaites[FEVES_MQ].getValeur()));
        demandes[FEVE_MQ_EQ3].setDemande_Prix(this.offrePublique[2].getPrix());

        return demandes;

    }

    @Override
    public void sendContratFictif(ContratFeve[] listContrats) {
    }

    @Override
    public void sendOffreFinale(ContratFeve[] offreFinale) {
        ContratFeve[] c1 = new ContratFeve[3];

        // Pour récupérer les offres qui nous intéressent
        int i = 0;
        for (ContratFeve c : offreFinale) {
            if ((((Eq2PROD) c.getProducteur()).getNom() == "Eq2PROD" && c.getQualite() == 0)

                    || (((Eq2PROD) c.getProducteur()).getNom() == "Eq2PROD" && c.getQualite() == 1)
                    || (((Eq3PROD) c.getProducteur()).getNom() == "Eq3PROD" && c.getQualite() == 1)) {
                c1[i] = c;
                i++;
            }

        }

        this.offrePublique = new ContratFeve[3];
        for (int j = 0; j < 3; j++) {


            if (((Eq2PROD) c1[j].getProducteur()).getNom() == "Eq2PROD") {
                this.offrePublique[0] = c1[j];
            }
            ;
            if (((Eq2PROD) c1[j].getProducteur()).getNom() == "Eq2PROD" && c1[j].getQualite() == 1) {
                this.offrePublique[1] = c1[j];
            } else {
                this.offrePublique[2] = c1[j];
            }
        }
    }

    @Override
    public ContratFeve[] getResultVentes() {
        return null;
    }

    /**
     * @author Maxim
     */
    @Override
    public double getReponse(DemandeAO d) {
        switch (d.getQualite()) {
            case 1: {
                journal.ajouter("Eq5 renvoie MAX_VALUE à getReponse(d)");
                return Double.MAX_VALUE;
            }
            case 2:
                if (d.getQuantite() < 0.2 * stocks[FRIANDISES_MQ].getValeur()) {
                    journal.ajouter("Eq5 renvoie" + 1.1 * prix[FRIANDISES_MQ].getValeur() * d.getQuantite() + "à getReponse(d)");
                    return 1.1 * prix[FRIANDISES_MQ].getValeur() * d.getQuantite();
                }
            case 3: {
                journal.ajouter("Eq5 renvoie MAX_VALUE à getReponse(d)");
                return Double.MAX_VALUE;
            }
            case 4:
                if (d.getQuantite() < 0.2 * stocks[TABLETTES_BQ].getValeur()) {
                    journal.ajouter("Eq5 renvoie" + 1.1 * prix[TABLETTES_BQ].getValeur() * d.getQuantite() + "à getReponse(d)");
                    return 1.1 * prix[TABLETTES_BQ].getValeur() * d.getQuantite();
                }
            case 5:
                if (d.getQuantite() < 0.2 * stocks[TABLETTES_MQ].getValeur()) {
                    journal.ajouter("Eq5 renvoie" + 1.1 * prix[TABLETTES_MQ].getValeur() * d.getQuantite() + "à getReponse(d)");
                    return 1.1 * prix[TABLETTES_MQ].getValeur() * d.getQuantite();
                }
            case 6:
                if (d.getQuantite() < 0.2 * stocks[TABLETTES_HQ].getValeur()) {
                    journal.ajouter("Eq5 renvoie" + 1.1 * prix[TABLETTES_HQ].getValeur() * d.getQuantite() + "à getReponse(d)");
                    return 1.1 * prix[TABLETTES_HQ].getValeur() * d.getQuantite();
                }
        }
        return Double.MAX_VALUE;
    }


}
