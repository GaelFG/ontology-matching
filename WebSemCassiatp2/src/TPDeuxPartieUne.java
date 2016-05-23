import java.net.URI;
import java.net.URISyntaxException;
import java.util.Properties;

import org.semanticweb.owl.align.Alignment;
import org.semanticweb.owl.align.AlignmentException;
import org.semanticweb.owl.align.AlignmentProcess;

import fr.inrialpes.exmo.align.impl.eval.PRecEvaluator;
import fr.inrialpes.exmo.align.parser.AlignmentParser;

public class TPDeuxPartieUne {


	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		System.out.println("hello");
		TPDeuxPartieUne prog = new TPDeuxPartieUne();
		prog.questionUneGenerationDAlignements();
	}

	public void questionUneGenerationDAlignements() throws Exception {
		URI UriFilmsToulouseOntologie = new URI("http://www.irit.fr/~Cassia.Trojahn/insa/FilmsToulouse.owl");
		URI UriNotreOntologie = new URI("http://oaei.ontologymatching.org/tests/302/onto.rdf");//TODO METTRE VRAIE URL
		URI UriDBPediaOntologie = new URI("http://mappings.dbpedia.org/server/ontology/dbpedia.owl");
		
		// paire d'ontologie (votre ontologie et FilmsToulouse)
		traiterUnePaireDOntologies(UriNotreOntologie, UriFilmsToulouseOntologie);

		// paire d'ontologie (votre ontologie et DBPedia)
		traiterUnePaireDOntologies(UriNotreOntologie, UriDBPediaOntologie);
	}
	
	// On genere un alignement et on en check la qualité selon trois methides differentes
	public void traiterUnePaireDOntologies(URI ontologieA, URI ontologieB){
		Alignment alignement;
		//choisissez l'un des matcheurs implementé par l'API d'Alignement et que vous avez 
		 //testé lors du premier TP ; 
		alignement = genererAlignement(ontologieA, ontologieB, MONALIGNEURMAGUEULE);
		
		// 2. développez le matcheur basé sur la comparaison de labels indiqué ci-dessous ;
		alignement = genererAlignement(ontologieA, ontologieB, MONALIGNEURMAGUEULE);
		 
		 //3. utilisez le matcheur LogMap, disponible
		 //sur http://www.cs.ox.ac.uk/isg/projects/LogMap/.
		alignement = genererAlignement(ontologieA, ontologieB, MONALIGNEURMAGUEULE);
		
	}
	
	/**
	 * Genere l'alignement entre deux ontologies.
	 * @param onto1 La première ontologie
	 * @param onto2 la deuxième ontologie
	 * @param process La methode d'alignement a utiliser
	 * @return Un alignement d'ontologies
	 * @throws Exception
	 */
	public static Alignment genererAlignement(URI onto1, URI onto2, AlignmentProcess process) throws Exception {
		process.init (onto1, onto2); 
		process.align(null, new Properties());
		return process;
	}
	
	public static void evaluerAlignement(Alignment alignment) throws Exception { 
	URI reference = new URI("http://oaei.ontologymatching.org/tests/302/refalign.rdf");
	AlignmentParser aparser = new AlignmentParser(0);
	Alignment refalign = aparser.parse(reference);
	PRecEvaluator evaluator = new PRecEvaluator(refalign, alignment); 
	evaluator.eval(new Properties());
	System.out.println("Precision : " + evaluator.getPrecision()); 
	System.out.println("Recall :" + evaluator.getRecall()); 
	System.out.println("FMeasure :" + evaluator.getFmeasure());
	}
}
