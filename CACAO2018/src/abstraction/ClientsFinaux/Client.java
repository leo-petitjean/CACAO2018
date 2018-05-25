/**
 * @author Leo Vuylsteker / Axelle Hermelin 
 */

package abstraction.ClientsFinaux;

import java.util.Random;

public class Client {
	private double[][] PartdeMarche;
 
	public Client(double[][] P) {
		if (P.length == 2 && P[0].length == 3) {
			this.PartdeMarche = P;
		} else { 
			this.PartdeMarche = new double[2][3];
		}
	}

	public Client() {
		this.PartdeMarche = new double[2][3];
	}

	public void Modifier(int i, int j, double valeur) {
		this.PartdeMarche[i][j] = valeur;
	}

	public double getValeur(int i, int j) {
		return this.PartdeMarche[i][j];
	}

	public double[][] getPartdeMarche() {
		return this.PartdeMarche;
	}

	public GrilleQuantite Generer(GrilleQuantite CommandeTotale) {
		double[][] commande = new double[2][3];
		for (int i = 0; i < commande.length; i++) {
			for (int j = 0; j < commande[0].length; j++) {
				commande[i][j] = this.getValeur(i, j) * CommandeTotale.getValeur(i, j);
			}
		}
		return new GrilleQuantite(commande);
	}

	public static GrilleQuantite CommandeTotale(GrilleQuantite StockTotal) {
		double[][] CommandeTotale = new double[2][3];
		for (int i = 0; i < CommandeTotale.length; i++) {
			for (int j = 0; j < CommandeTotale[0].length; j++) {
				CommandeTotale[i][j] = 0.7 * StockTotal.getValeur(i, j)
						+ (Math.random() * (0.6 * StockTotal.getValeur(i, j)));
			}
		}
		
		return new GrilleQuantite(CommandeTotale);
	}
}