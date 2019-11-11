package agent.rlapproxagent;

import agent.rlagent.QLearningAgent;
import agent.rlagent.RLAgent;
import environnement.Action;
import environnement.Environnement;
import environnement.Etat;
/**
 * Agent qui apprend avec QLearning en utilisant approximation de la Q-valeur : 
 * approximation lineaire de fonctions caracteristiques 
 * 
 * @author laetitiamatignon
 *
 */
public class QLApproxAgent extends QLearningAgent{
	
	private FeatureFunction _featurefunction;
	private double[] weights;
	
	public QLApproxAgent(double alpha, double gamma, Environnement _env,FeatureFunction _featurefunction) {
		super(alpha, gamma, _env);
		
		//*** VOTRE CODE
		this._featurefunction = _featurefunction;
		weights = new double[_featurefunction.getFeatureNb()];
	}

	
	@Override
	public double getQValeur(Etat e, Action a) {
		//*** VOTRE CODE
		double[] features = _featurefunction.getFeatures(e, a);
		
		double sum = 0;
		
		for (int i = 0 ; i != features.length ; i++) {
			sum = sum + features[i] * weights[i];
		}
		
		return sum;
	}
	
	
	
	
	@Override
	public void endStep(Etat e, Action a, Etat esuivant, double reward) {
		if (RLAgent.DISPRL){
			System.out.println("QL: mise a jour poids pour etat \n"+e+" action "+a+" etat' \n"+esuivant+ " r "+reward);
		}
       //inutile de verifier si e etat absorbant car dans runEpisode et threadepisode 
		//arrete episode lq etat courant absorbant	
		
		//*** VOTRE CODE
		double valeurSuivant = getValeur(esuivant);
		double valeurArc = getQValeur(e, a);
		double coefficientMultiplicateur =
				alpha * (reward + gamma * valeurSuivant - valeurArc);

		double[] feature = _featurefunction.getFeatures(e, a);
		for (int i = 0 ; i != weights.length ; i++) {
			weights[i] += coefficientMultiplicateur * feature[i];
		}
	}
	
	@Override
	public void reset() {
		super.reset();
		this.qvaleurs.clear();
	
		//*** VOTRE CODE
		
		for (int i = 0 ; i != weights.length ; i++) {
			weights[i] = 0.0;
		}
		
		this.episodeNb =0;
		this.notifyObs();
	}
}
