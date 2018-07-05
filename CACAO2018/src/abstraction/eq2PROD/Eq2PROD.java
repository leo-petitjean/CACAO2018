package abstraction.eq2PROD;

import abstraction.fourni.*;
import abstraction.eq3PROD.echangesProdTransfo.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import abstraction.eq2PROD.acheteurFictifTRAN.acheteurFictifTRAN;
import abstraction.eq2PROD.echangeProd.*;

public class Eq2PROD implements Acteur, IVendeurFevesProd, IVendeurFeveV4 {
// VARIABLES D'INSTANCE 
	private int stockQM;
	private int stockQB;
	private double solde;
	private boolean maladie;
	private double coeffStock;
	private List<ContratFeveV3> demandeTran;
	private final static int MOY_QB = 46000; // pour un step = deux semaines
	private final static int MOY_QM = 70000; // pour un step = deux semaines
	private final static int coutFixe = 70800000; // entretien des plantations
	private final static double prix_minQM = 1000;
	private final static double prix_minQB = 850;
	private boolean quantiteEq3;
	private Indicateur indicateurQB;
	private Indicateur indicateurQM;
	private Indicateur soldejournal;
	private double totalVenteQB;
	private double totalVenteQM;
	public final static double ponderation = 0.1;
	public final static Pays[] listPays = new Pays[] { Pays.COTE_IVOIRE, Pays.GHANA, Pays.NIGERIA, Pays.CAMEROUN, 
			Pays.OUGANDA, Pays.TOGO, Pays.SIERRA_LEONE, Pays.MADAGASCAR, Pays.LIBERIA, Pays.TANZANIE }  ;
	public final static double[]  partProd = new double[] { 0.48, 0.28, 0.12, 0.09, 0.006, 0.005, 0.005, 0.003, 0.003,
			0.002} ;
	public final static double[] coeffInstable = new double[] {0.36, 0.68, 0.05, 0.52, 0.42, 0.55, 0.63, 0.46, 0.54, 0.63};
	public final static int[][] paysFront = new int[][] { {/*Pays.LIBERIA*/ 8 , /*Pays.GHANA*/ 1},
											{/*Pays.COTE_IVOIRE*/0, /*Pays.TOGO*/5}, 
											{/*Pays.CAMEROUN*/3, /*Pays.TOGO*/6},
											{/*Pays.NIGERIA*/2},
											{/*Pays.TANZANIE*/9},
											{/*Pays.GHANA*/1, /*Pays.NIGERIA*/2},
											{/*Pays.LIBERIA*/8},
											{/*Pays.TANZANIE*/9},
											{/*Pays.SIERRA_LEONE*/6, /*Pays.COTE_IVOIRE*/0},
											{/*Pays.OUGANDA*/4, /*Pays.MADAGASCAR*/7}};
	
	
	public boolean[] estInstable;
	public double[] coeffDeficitProd;
	
	
// CONSTRUCTEURS
	public Eq2PROD() {
		
		setNomEq("Eq2PROD");
		this.estInstable = new boolean[10] ;
		for (int i=0; i<10; i++) {
			this.estInstable[i]=false;
		}
		this.coeffDeficitProd = new double[10] ;
		for (int i=0; i<10; i++) {
			this.coeffDeficitProd[i]=0.0 ;
		}
	
		setStockAffichage();
		this.indicateurQB = new Indicateur("Stock de "+getNomEq()+" de basse qualité",this,getStockQB());
		this.indicateurQM = new Indicateur("Stock de "+getNomEq()+" de moyenne qualité",this,getStockQM());
		this.soldejournal = new Indicateur("Solde de"+getNomEq(), this, getSolde());
		setStockQBas(indicateurQB);
		setStockQMoy(indicateurQM);
		
		setJournal(new Journal("Journal de"+getNomEq()));
		setJournalOccasionel(new Journal("Journal de ventes occasionnelles de"+getNomEq()));
		Monde.LE_MONDE.ajouterJournal(getJournal());
		Monde.LE_MONDE.ajouterJournal(getJournalOccasionel());
		Monde.LE_MONDE.ajouterIndicateur(getStockQBas());
		Monde.LE_MONDE.ajouterIndicateur(getStockQMoy());
		Monde.LE_MONDE.ajouterIndicateur(getSoldeJournal());
		
		Monde.LE_MONDE.ajouterActeur(new acheteurFictifTRAN());
		this.nomEq = "Eq2PROD";
		this.stockQM=0;
		this.stockQB=0;
		this.solde = 4070800000.0;
		this.maladie = false;
		this.coeffStock = 1;
		this.demandeTran = new ArrayList<>();
		this.quantiteEq3 = false;
		this.totalVenteQB=0.0;
		this.totalVenteQM=0.0;
		
	}
	
// GETTEURS
	/* Guillaume Sallé (jusqu'à getNom())*/
	public int getStockQM() {
		return this.stockQM;
	}
	public int getStockQB() {
		return this.stockQB;
	}
	public double getSolde() {
		return this.solde;
	}
	public double getTotalVenteQB() {
		return this.totalVenteQB;
	}
	public double getTotalVenteQM() {
		return this.totalVenteQM;
	}
	public void setTotalVenteQB(double q) {
		this.totalVenteQB+=q;
	}
	public void razTotalVenteQB() {
		this.totalVenteQB = 0;
	}
	public void setTotalVenteQM(double q) {
		this.totalVenteQM+=q;
	}
	public void razTotalVenteQM() {
		this.totalVenteQM = 0;
	}
	public void addSolde(double s) {
		this.solde = this.solde + s;
	}
	public void retireSolde(double s) {
		this.solde = this.solde - s;
	}
	public void addStockQB(int s) {
		this.stockQB = this.stockQB + s;
	}
	public void retireStockQB(int s) {
		this.stockQB = this.stockQB - s;
	}
	public void addStockQM(int s) {
		this.stockQM = this.stockQM + s;
	}
	public void retireStockQM(int s) {
		this.stockQM = this.stockQM - s;
	}
	// Coeff pour l'augmentation du stock (en fct de meteo et maladie), calculé ligne 156
	public double getCoeffStock() {
		return this.coeffStock;
	}
	// Coeff pour le calcul des prixOffrePublique dans les contrats (plus on récolte, moins c'est cher).
	public double getCoeffSolde() {
		return 2.0-getCoeffStock();
	}
	public void setCoeffStock(double x) {
		this.coeffStock = x;
	}
	public boolean getQuantiteEq3() {
		return this.quantiteEq3;
	}
	public void setQuantiteEq3(boolean b) {
		this.quantiteEq3 = b;
	}
	
	public double[] getListeInstabilite() {
		return coeffDeficitProd ;
	} 
	/* implementé en V0 */
	public String getNom() {
		return "Eq2PROD";
	}
	/* Romain Bernard */
	public List<ContratFeveV3> getDemandeTran() {
		return this.demandeTran;
	}
	public void setDemandeTran(List<ContratFeveV3> demande) {
		this.demandeTran = demande;
	}
	/* Alexandre Bigot + Guillaume Sallé*/
	public double getPrix() {
		return prixMarche()*getCoeffSolde()*1.06 ;
	}
	/* Alexandre Bigot + Guillaume Sallé */
	public double getCoeffMeteo() {
		return this.meteo();
	}
	/* Alexandre Bigot */
	public boolean getCoeffMaladie() {
		return this.maladie;
	}
	/* Guillaume Sallé */
	public void setCoeffMaladie(boolean b) {
		this.maladie = b;
	}
	
/* VARIABLES INDEPENDANTES DES AUTRES GROUPES
 * Ces variables nous permettent de modeliser une production de cacao
 * la plus réaliste possible par rapport à nos recherches documentaires
 * precedant la modelisation informatique
 * Nous prenons en compte la meteo et les maladies
 */
	private double meteo() {
		/* Modélisation par Guillaume Sallé + Agathe Chevalier + Alexandre Bigot
		 * Code par Guillaume Sallé */
		double mini = 0.5;
		double maxi = 1.3;
		double x = Math.random();
		if (x<0.005) {
			return mini;
		} else if (x>0.995) {
			return maxi;
		} else {
			return 0.303*x+0.848;
		}
	}
	
	/* Modélisation par Alexandre Bigot + Guillaume Sallé, 
	 * Code par Alexandre Bigot
	 * Si plantations déjà malades alors la récolte est diminuée de 50% par rapport à la récolte
	 * déjà réduite ou augmentée par la météo et au step suivant la plantation n'est plus malade
	 * sinon il y a 0.5% que la plantation soit infectée et la récolte n'est pas diminuée par 
	 * le facteur maladie  */
	private double maladie() {
		if (getCoeffMaladie()) {
			setCoeffMaladie(false);
			return 0.5;
		} else {
			double x=Math.random();
			if (x<0.05) {
				setCoeffMaladie(true);
			}
			return 0.0 ;
		}
	}
	
	/* code par Alexandre BIGOT */
	public void chgmtInstable(int p) { //* p est un entier entre 0 et 9
		double proba = (1-coeffInstable[p])*ponderation ;
		if (this.estInstable[p]) {
			proba=proba*2 ;     /*Si le pays est déjà instable, il a plus de chance de rester instable (2x plus) */
		}
		if (Math.random()<proba) {
			this.estInstable[p]=true ;
		} else {
			this.estInstable[p]=false ;
		}
	}
	
	/* code par Alexandre BIGOT */
	public void propageInstable(int p) {
		for (int i=0; i<paysFront[p].length;i++) {
			double proba = (1-coeffInstable[paysFront[p][i]])*ponderation*2 ;
			if (Math.random()<proba) {
				estInstable[paysFront[p][i]] = true ;
			} else {
				estInstable[paysFront[p][i]] = false ;
			}
		}
	}
	
	/* code par Alexandre BIGOT */
	public void  majStabilite() { /* maj de la stabilité des 10 pays par chgt spontanée et propagation
		et maj du coefficient qui réduit la production quand la stabilité est mauvaise */
			for (int i=0; i<=9;i++) {
				chgmtInstable(i);
			}
			for (int i=0; i<=9; i++) {
				if (estInstable[i]) propageInstable(i);
			}
			for (int i=0;i<10;i++) {
				 if (estInstable[i]) {
					 coeffDeficitProd[i]=0.35 ;
				 }
			}
			}
	
	public double coeffFinal() {
		double c = 0;
		for (int i=0; i<10 ; i++) {
			c = c + coeffDeficitProd[i];
		}
		return c ;
	} 
	
	/* Alexandre Bigot + Guillaume Sallé */
	private void calculCoeffStock() {
		double coeffMeteo = meteo();
		double coeffMaladie = maladie();
		double coeffInstabilite = 0 ;
		if (Monde.LE_MONDE.getStep()%4==0) {
			majStabilite();
			coeffInstabilite = coeffFinal();
		} 
		setCoeffStock((-0.2*(coeffMeteo-coeffMaladie)+1.2)*coeffInstabilite);
	}
	
/* IMPLEMENTATION DES DIFFERENTES INTERFACES UTILES A NOTRE ACTEUR
 * Ici nous avons implemente IVendeurFeve et IVendeurFevesProd */
	
	/* Alexandre Bigot
	 * Le cas où la quantité demandée est inférieure au stock n'est au final pas codée
	 * car il est théoriquement impossible que cela arrive :
	 * les producteurs représentent tout le marché mondial mais pas les transformateurs.
	 */
	public int acheter(int quantite) {
		if (quantite <= getStockQM()) {
			retireStockQM(quantite);
			addSolde(quantite*getPrix()) ;
			setQuantiteEq3(true);
			return quantite ;
		} else {
			setQuantiteEq3(false);
			return 0 ;
		}
	}
	
	/* Romain Bernard */
	private double prixMarche() {
		ArrayList<Acteur> acteurs=Monde.LE_MONDE.getActeurs();
		for (Acteur a : acteurs) {
			if(a instanceof MarcheFeve) {
				return ((MarcheFeve) a).getPrixMarche();
			} 
		} return 0;
	}  
		
/* INDICATEURS ET JOURNAUX
 * Les indicateurs que nous affichons sont le stock de basse qualite et le stock de moyenne qualite
 * Nous avons 2 journaux: un pour l'historique des stocks, la meteo et les maladies
 * et un autre pour l'historique des echanges avec les autres producteurs
 */
	/* Agathe Chevalier + Alexandre Bigot */
	private Journal journal;
	private Journal ventesOccasionnelles;
	private String nomEq;
	private Indicateur stockQMoy;
	private Indicateur stockQBas;
	
	/* Agathe Chevalier */
	public Journal getJournal() {
		return this.journal;
	}
	public String getNomEq() {
		return this.nomEq;
	}
	public void setNomEq(String s) {
		this.nomEq = s;
	}
	/* Alexandre Bigot */
	public Journal getJournalOccasionel() {
		return this.ventesOccasionnelles;
	}
	public void setJournalOccasionel(Journal j) {
		this.ventesOccasionnelles = j;
	}
	/* Guillaume Sallé */
	public void setJournal(Journal j) {
		this.journal = j;
	}
	public Indicateur getStockQBas() {
		return this.stockQBas;
	}
	public void setStockQBas(Indicateur i) {
		this.stockQBas = i;
	}
	public Indicateur getSoldeJournal() {
		return this.soldejournal;
	}
	public void setSoldeJournal(Indicateur i) {
		this.soldejournal = i;
	}
	public Indicateur getStockQMoy() {
		return this.stockQMoy;
	}
	public void setStockQMoy(Indicateur i) {
		this.stockQMoy = i;
	}
	
	
	protected void setStockAffichage() {
		calculCoeffStock();
		addStockQM( (int) (getCoeffSolde()*MOY_QM));
		addStockQB( (int) (getCoeffSolde()*MOY_QB));
	}
	
	
// NEXT DE NOTRE ACTEUR
	/* Code par Guillaume SALLE + Agathe CHEVALIER */
	public void next() {
		retireSolde(coutFixe);
		this.getJournal().ajouter("Quantité vendue Basse Qualite ="+getTotalVenteQB());
		this.getJournal().ajouter("Quantité vendue Moyenne Qualité ="+getTotalVenteQM());
		this.getJournal().ajouter("Quantité basse qualité = "+ getStockQB());
		this.getJournal().ajouter("Quantité moyenne qualité ="+ getStockQM());
		this.getJournal().ajouter("Solde ="+getSolde()+" €");
		this.getJournal().ajouter("Coefficient de la météo ="+ getCoeffMeteo());
		this.getJournal().ajouter("Liste des coefficients d'instabilité de nos Pays : " + Arrays.toString(getListeInstabilite()));
		this.getJournal().ajouter("Le coefficient total de l'instabilité est : " + coeffFinal()); 
		if(!(getCoeffMaladie())) {
			this.getJournal().ajouter("Aucune maladie n'a frappé les plantations");
		} else {
			this.getJournal().ajouter("Une maladie a frappé les plantations");
		}
		this.getJournal().ajouter("------------------------------------------------------------------------------");
		if((getQuantiteEq3())) {
			this.getJournalOccasionel().ajouter("Une transaction a été réalisée avec l'équipe 3");
		} else {
			this.getJournalOccasionel().ajouter("Aucune transaction n'a été réalisée avec l'équipe3");
		}
		this.getJournalOccasionel().ajouter("------------------------------------------------------------------------------");
		setStockAffichage();
		indicateurQB.setValeur(this, getStockQB());
		indicateurQM.setValeur(this, getStockQM());
		soldejournal.setValeur(this, getSolde());
		razTotalVenteQB();
		razTotalVenteQM();
	}


// VERSION 4
	/* Code par Guillaume Sallé + Romain Bernard + Agathe Chevalier */
	public List<ContratFeveV3> getOffrePubliqueV3() {
		ContratFeveV3 c1 = new ContratFeveV3(null, this, 0, getStockQB(), 0, 0, 
				prixMarche()*getCoeffSolde()*0.85, 0.0, 0.0, false);
			ContratFeveV3 c2 =  new ContratFeveV3(null, this, 1, getStockQM(), 0, 0, 
				prixMarche()*getCoeffSolde(), 0.0, 0.0, false);
			List<ContratFeveV3> listContrat = new ArrayList<>();
			listContrat.add(c1);
			listContrat.add(c2);
			return listContrat;
	}
	
	/* Code par Guillaume Sallé + Romain Bernard + Agathe Chevalier */
	public void sendDemandePriveeV3(List<ContratFeveV3> demandePrivee) {
		setDemandeTran(demandePrivee); 		
	}

	/* Modélisation par Romain Bernard + Guillaume Sallé
	 * Code par Romain Bernard
	 * Revu par Guillaume Sallé pour la nouvelle implémentation */
	public List<ContratFeveV3> getOffreFinaleV3() {
		List<ContratFeveV3> c= new ArrayList<>();	
		for (ContratFeveV3 d : demandeTran) {
			c.add(d);
			// Les transfo ne représentent pas tout le marché : on peut toujours leur vendre la quantité demandée
			c.get(c.size()-1).setProposition_Quantite(c.get(c.size()-1).getDemande_Quantite());
			if (c.get(c.size()-1).getQualite()==0) {
				// Si leur prix est supérieur au notre, on prend le leur et on est content
				if (c.get(c.size()-1).getDemande_Prix()>=c.get(c.size()-1).getOffrePublique_Prix()) {
					c.get(c.size()-1).setProposition_Prix(c.get(c.size()-1).getDemande_Prix());
					// Si le prix est en-dessous de notre seuil de rentabilité, on propose notre seuil
				} 	else if (c.get(c.size()-1).getDemande_Prix()<prix_minQB) {
					c.get(c.size()-1).setProposition_Prix(prix_minQB);
					// Sinon on propose un prix intermédiaire à notre seuil et leur demande
				}	else {
					c.get(c.size()-1).setProposition_Prix(0.25*prix_minQB+0.75*c.get(c.size()-1).getDemande_Prix());
				}
			} // On fait pareil avec l'autre qualité (prix_minQB -> prix_minQM)
			else {
				if (c.get(c.size()-1).getDemande_Prix()>=c.get(c.size()-1).getOffrePublique_Prix()) {
					c.get(c.size()-1).setProposition_Prix(c.get(c.size()-1).getDemande_Prix());
				} else if (c.get(c.size()-1).getDemande_Prix()<prix_minQM) {
					c.get(c.size()-1).setProposition_Prix(prix_minQM);
				} else {
					c.get(c.size()-1).setProposition_Prix(0.25*prix_minQM+0.75*c.get(c.size()-1).getDemande_Prix());
				}
			}
		}
		return c;
	}
	
	/* Agathe Chevalier + Alexandre Bigot + Romain Bernard 
	 * Revu par Guillaume Sallé pour la nouvelle implémentation */
	public void sendResultVentesV3(List<ContratFeveV3> resultVentes) {
		double chiffreDAffaire=0;
    	for (ContratFeveV3 c : resultVentes) {
   		 // Si le contrat i est accepté par le transfo
   		   	if (c.getReponse()) {
   			 // On augmente notre solde et on diminue notre stock (QB ou QM)
   		   		if (c.getQualite()==0) {
   		   			addSolde(c.getProposition_Prix()*c.getProposition_Quantite()) ;
   		   			retireStockQB(c.getProposition_Quantite()) ;
   		   			chiffreDAffaire+=c.getProposition_Prix()*c.getProposition_Quantite();
   		   			setTotalVenteQB(c.getProposition_Quantite());
   		   		}
   		   		if (c.getQualite()==1) {
   		   			addSolde(c.getProposition_Prix()*c.getProposition_Quantite()) ;
   		   			retireStockQM(c.getProposition_Quantite()) ;
   		   			chiffreDAffaire+=c.getProposition_Prix()*c.getProposition_Quantite();
   		   			setTotalVenteQM(c.getProposition_Quantite());
   		   		} 
   		   	} 
   		 // On paye les salaires : 35% du CA
    	} retireSolde(0.35*chiffreDAffaire);		
	}
}
