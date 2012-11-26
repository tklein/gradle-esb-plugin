/**
 * 
 */
package eu.schnuckelig.gradle.plugins.jboss

import org.gradle.api.file.CopySpec
import org.gradle.api.file.FileCollection
import org.gradle.api.internal.file.copy.CopySpecImpl
import org.gradle.api.file.FileCopyDetails
import org.gradle.api.tasks.bundling.Jar
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.Optional

/**
 * @author Thorsten Klein
 *
 */
class Esb extends Jar {
    public static final String ESB_EXTENSION = 'esb'

    private FileCollection classpath
    private final CopySpecImpl metaInf

    Esb() {
        extension = ESB_EXTENSION
        // Add these as separate specs, so they are not affected by the changes to the main spec
        metaInf = copyAction.rootSpec.addChild().into('META-INF')
		// Copy class files
		copyAction.rootSpec.into('') {
			from {
				def classpath = getClasspath()
				classpath ? classpath.filter {File file -> file.isDirectory()} : []
			}
		}
		
//		copyAction.mainSpec.eachFile { FileCopyDetails details ->
//			if (details.path.equalsIgnoreCase('META-INF/MANIFEST.MF')) {
//				details.exclude()
//			}
//		}
		
		
		// Copy jars
		copyAction.rootSpec.into('') {
            from {
                def classpath = getClasspath()
                classpath ? classpath.filter {File file -> file.isFile()} : []
            }
        }
    }

    CopySpec getMetaInf() {
        return metaInf.addChild()
    }

    /**
     * Adds some content to the {@code META-INF} directory for this ESB archive.
     *
     * <p>The given closure is executed to configure a {@link CopySpec}. The {@code CopySpec} is passed to the closure
     * as its delegate.
     *
     * @param configureClosure The closure to execute
     * @return The newly created {@code CopySpec}.
     */
    CopySpec metaInf(Closure configureClosure) {
        return ConfigureUtil.configure(configureClosure, getMetaInf())
    }

    /**
     * Returns the classpath to include in the ESB archive. Any JAR or ZIP files in this classpath are included in the
     * root directory. Any directories in this classpath are included in the root
     * directory.
     *
     * @return The classpath. Returns an empty collection when there is no classpath to include in the ESB.
     */
    @InputFiles 
	@Optional
    FileCollection getClasspath() {
        return classpath
    }

    /**
     * Sets the classpath to include in the ESB archive.
     *
     * @param classpath The classpath. Must not be null.
     */
    void setClasspath(Object classpath) {
        this.classpath = project.files(classpath)
    }

    /**
     * Adds files to the classpath to include in the ESB archive.
     *
     * @param classpath The files to add. These are evaluated as for {@link org.gradle.api.Project#files(Object [])}
     */
    void classpath(Object... classpath) {
        FileCollection oldClasspath = getClasspath()
        this.classpath = project.files(oldClasspath ?: [], classpath)
    }
}