import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.URI;
import java.util.Properties;

import org.semanticweb.owl.align.Alignment;
import org.semanticweb.owl.align.AlignmentProcess;
import org.semanticweb.owl.align.AlignmentVisitor;

import fr.inrialpes.exmo.align.impl.method.ClassStructAlignment;
import fr.inrialpes.exmo.align.impl.renderer.RDFRendererVisitor;

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
		traiterUnePaireDOntologies(UriNotreOntologie, UriFilmsToulouseOntologie, "films-toulouse-");

		// paire d'ontologie (votre ontologie et DBPedia)
		traiterUnePaireDOntologies(UriNotreOntologie, UriDBPediaOntologie, "dbpedia-");
	}
	
	// On genere un alignement et on en check la qualité selon trois methides differentes
	public void traiterUnePaireDOntologies(URI ontologieA, URI ontologieB, String prefixNomFichier) throws Exception{
		Alignment alignement;
		//choisissez l'un des matcheurs implementé par l'API d'Alignement et que vous avez 
		 //testé lors du premier TP ; 
		alignement = genererAlignement(ontologieA, ontologieB, new ClassStructAlignment());
		render(alignement, "./alignement"+prefixNomFichier+"-de-base.rdf");
		
		// 2. développez le matcheur basé sur la comparaison de labels indiqué ci-dessous ;
		//TODO alignement = genererAlignement(ontologieA, ontologieB, MONALIGNEURMAGUEULE);
		render(alignement, "./alignement"+prefixNomFichier+"-comparaison-label.rdf");
		
		 //3. utilisez le matcheur LogMap, disponible
		 //sur http://www.cs.ox.ac.uk/isg/projects/LogMap/.
		//TODO alignement = genererAlignement(ontologieA, ontologieB, MONALIGNEURMAGUEULE);
		render(alignement, "./alignement"+prefixNomFichier+"-logmap.rdf");
		
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
	
	/**
	 * Génére le fichier d'alignement rdf .
	 * @param alignment L'alignement à exporter sous forme de fichier
	 * @param cheminFichier Le chemin du fichier à générer
	 * @throws Exception Kancamarchepa
	 */
	public static void render(Alignment alignment, String cheminFichier) throws Exception { 
		PrintWriter writer; 
		FileOutputStream f = new FileOutputStream(new File(cheminFichier)); 
		writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(f,"UTF-8")), true); 
		AlignmentVisitor renderer = new RDFRendererVisitor(writer); 
		alignment.render(renderer); 
		writer.flush(); 
		writer.close();	
	}
}
