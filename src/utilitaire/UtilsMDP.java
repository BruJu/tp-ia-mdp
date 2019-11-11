package utilitaire;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * Fonctions statiques utilitaires
 */
public class UtilsMDP {
	
	/**
	 * Filtre les éléments de la liste pour renvoyer une liste ne contenant que les meilleurs éléments
	 * @param values Les éléments
	 * @param evaluation La fonction d'évaluation d'un élément
	 * @return La liste des éléments dont l'évaluation est la meilleure
	 */
	public static <T> List<T> filterBest(List<T> values, Function<T, Double> evaluation) {
		List<T> best = new ArrayList<>();
		
		Double bestValue = null;
		
		for (T value : values) {
			Double eval = evaluation.apply(value);
			
			if (eval == null)
				continue;
			
			if (bestValue != null) {
				if (eval < bestValue)
					continue;
				
				if (eval > bestValue)
					best.clear();
			}
			
			best.add(value);
			bestValue = eval;
		}
		
		return best;
	}
}
