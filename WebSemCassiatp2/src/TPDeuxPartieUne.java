import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Properties;

import org.semanticweb.owl.align.Alignment;
import org.semanticweb.owl.align.AlignmentException;
import org.semanticweb.owl.align.AlignmentProcess;
import org.semanticweb.owl.align.AlignmentVisitor;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import fr.inrialpes.exmo.align.impl.eval.PRecEvaluator;
import fr.inrialpes.exmo.align.impl.method.EditDistNameAlignment;
import fr.inrialpes.exmo.align.impl.renderer.RDFRendererVisitor;
import fr.inrialpes.exmo.align.parser.AlignmentParser;
import fr.inrialpes.exmo.ontowrap.OntowrapException;

public class TPDeuxPartieUne {


	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println("hello");
		TPDeuxPartieUne prog = new TPDeuxPartieUne();
		try {
			prog.questionUneGenerationDAlignements();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void questionUneGenerationDAlignements() throws Exception {
		URI UriFilmsToulouseOntologie = new URI("http://www.irit.fr/~Cassia.Trojahn/insa/FilmsToulouse.owl");
		URI UriNotreOntologie = new URI("file:///C:/Users/gwend/git/tpcassia/WebSemCassiatp2/maFilmographie.owl");
		URI UriDBPediaOntologie = new URI("http://mappings.dbpedia.org/server/ontology/dbpedia.owl");
		
		URI UriAlignementRefLogMapFilmsToulouse = new URI("file:///C:/Users/gwend/git/tpcassia/WebSemCassiatp2/alignementfilms-toulouse--referencelogmap.rdf");
		URI UriAlignementRefLogMapDBPedia = new URI("file:///C:/Users/gwend/git/tpcassia/WebSemCassiatp2/alignementdbpedia--referencelogmap.rdf");
		
		// paire d'ontologie (votre ontologie et FilmsToulouse)
		
		
		traiterUnePaireDOntologies(UriNotreOntologie, UriFilmsToulouseOntologie, "films-toulouse-", UriAlignementRefLogMapFilmsToulouse);
		// paire d'ontologie (votre ontologie et DBPedia)
		traiterUnePaireDOntologies(UriNotreOntologie, UriDBPediaOntologie, "dbpedia-", UriAlignementRefLogMapDBPedia);
	}
	
	// On genere un alignement et on en check la qualité selon trois methides differentes
	public void traiterUnePaireDOntologies(URI ontologieA, URI ontologieB, String prefixNomFichier, URI uriAlignementReference) throws FileNotFoundException, UnsupportedEncodingException, AlignmentException, OWLOntologyCreationException, OntowrapException, URISyntaxException{
		Alignment alignement;
		//choisissez l'un des matcheurs implementé par l'API d'Alignement et que vous avez 
		 //testé lors du premier TP ; 
		//TODO penser a preciser recherhe de la methode d'alignement apes plusieur essais blabla
		AlignmentProcess alignementProcess = new EditDistNameAlignment();
		//alignementProcess.cut(0.8);//TODO apparament ne fait rien en fait
		alignement = genererAlignement(ontologieA, ontologieB, alignementProcess);
		//Ce generateur genere beaucoup trop de faux positifs, on demande une précision minimum empirique.
		alignement.cut(0.8);
		System.out.println("Alignement 1 (editDistName) : " + alignement.nbCells());
		
		evaluate(alignement, uriAlignementReference);
		render(alignement, "./alignement"+prefixNomFichier+"-de-base.rdf");
		
		// 2. développez le matcheur basé sur la comparaison de labels indiqué ci-dessous ;
		MatcheurLabels matcheurLabelProcess= new MatcheurLabels();
		matcheurLabelProcess.init (ontologieA, ontologieB); 
		matcheurLabelProcess.align(null, new Properties());
		System.out.println("Alignement 2 (parseur manuel): " + matcheurLabelProcess.nbCells());
		evaluate(alignement, uriAlignementReference);
		render(matcheurLabelProcess, "./alignement"+prefixNomFichier+"-comparaison-label.rdf");
		
		
	}
	
	/**
	 * Genere l'alignement entre deux ontologies.
	 * @param onto1 La première ontologie
	 * @param onto2 la deuxième ontologie
	 * @param process La methode d'alignement a utiliser
	 * @return Un alignement d'ontologies
	 * @throws AlignmentException 
	 * @throws Exception
	 */
	public static Alignment genererAlignement(URI onto1, URI onto2, AlignmentProcess process) throws AlignmentException {
		process.init (onto1, onto2); 
		process.align(null, new Properties());
		return process;
	}
	
	/**
	 * Compare un alignement avec une référence et affiche ses valeurs de precision, recall et FMeaseure
	 * @param alignment L'alignement à analyser
	 * @param reference L'uri d'un fichier d'alignement rdf de reference
	 * @throws URISyntaxException
	 * @throws AlignmentException
	 */
	public static void evaluate(Alignment alignment, URI reference) throws URISyntaxException, AlignmentException {
	AlignmentParser aparser = new AlignmentParser(0);
	Alignment refalign = aparser.parse(reference);
	PRecEvaluator evaluator = new PRecEvaluator(refalign, alignment); 
	evaluator.eval(new Properties());
	System.out.println("Precision : " + evaluator.getPrecision()); 
	System.out.println("Recall :" + evaluator.getRecall()); 
	System.out.println("FMeasure :" + evaluator.getFmeasure());
	}
	
	/**
	 * Génére le fichier d'alignement rdf .
	 * @param alignment L'alignement à exporter sous forme de fichier
	 * @param cheminFichier Le chemin du fichier à générer
	 * @throws AlignmentException 
	 * @throws FileNotFoundException 
	 * @throws UnsupportedEncodingException 
	 * @throws Exception Kancamarchepa
	 */
	public static void render(Alignment alignment, String cheminFichier) throws AlignmentException, FileNotFoundException, UnsupportedEncodingException { 
		PrintWriter writer; 
		FileOutputStream f = new FileOutputStream(new File(cheminFichier)); 
		writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(f,"UTF-8")), true); 
		AlignmentVisitor renderer = new RDFRendererVisitor(writer); 
		alignment.render(renderer); 
		writer.flush(); 
		writer.close();	
	}
}
