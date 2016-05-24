import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.Properties;

import org.semanticweb.owl.align.Alignment;
import org.semanticweb.owl.align.AlignmentException;
import org.semanticweb.owl.align.AlignmentProcess;
import org.semanticweb.owl.align.AlignmentVisitor;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import fr.inrialpes.exmo.align.impl.method.EditDistNameAlignment;
import fr.inrialpes.exmo.align.impl.renderer.RDFRendererVisitor;
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
		//URI UriNotreOntologie = new URI("http://mappings.dbpedia.org/server/ontology/dbpedia.owl");
		URI UriNotreOntologie = new URI("file:///C:/Users/yanni_000/git/ontology-matching/WebSemCassiatp2/maFilmographie.owl");//TODO METTRE VRAIE URL
		URI UriDBPediaOntologie = new URI("http://mappings.dbpedia.org/server/ontology/dbpedia.owl");
		
		// paire d'ontologie (votre ontologie et FilmsToulouse)
		traiterUnePaireDOntologies(UriNotreOntologie, UriFilmsToulouseOntologie, "films-toulouse-");

		// paire d'ontologie (votre ontologie et DBPedia)
		traiterUnePaireDOntologies(UriNotreOntologie, UriDBPediaOntologie, "dbpedia-");
	}
	
	// On genere un alignement et on en check la qualité selon trois methides differentes
	public void traiterUnePaireDOntologies(URI ontologieA, URI ontologieB, String prefixNomFichier) throws FileNotFoundException, UnsupportedEncodingException, AlignmentException, OWLOntologyCreationException, OntowrapException{
		Alignment alignement;
		//choisissez l'un des matcheurs implementé par l'API d'Alignement et que vous avez 
		 //testé lors du premier TP ; 
		//TODO penser a preciser recherhe de la methode d'alignement apes plusieur essais blabla
		AlignmentProcess alignementProcess = new EditDistNameAlignment();
		//alignementProcess.cut(0.8);//TODO apparament ne fait rien en fait
		alignement = genererAlignement(ontologieA, ontologieB, alignementProcess);
		//Ce generateur genere beaucoup trop de faux positifs, on demande une précision minimum empirique.
		alignement.cut(0.8);
		System.out.println("Alignement 1 : " + alignement.nbCells());
		render(alignement, "./alignement"+prefixNomFichier+"-de-base.rdf");
		
		// 2. développez le matcheur basé sur la comparaison de labels indiqué ci-dessous ;
		MatcheurLabels matcheurLabelProcess= new MatcheurLabels();
		matcheurLabelProcess.init (ontologieA, ontologieB); 
		matcheurLabelProcess.align(null, new Properties());
		matcheurLabelProcess.cut(0.8);
		System.out.println("Alignement 2 : " + matcheurLabelProcess.nbCells());
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
		//System.out.println(process.nbCells());
		return process;
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
