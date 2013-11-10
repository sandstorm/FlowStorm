package de.sandstormmedia.flowstorm;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;
import com.jetbrains.php.PhpIndex;
import com.jetbrains.php.lang.psi.elements.MethodReference;
import com.jetbrains.php.lang.psi.elements.PhpNamedElement;
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression;
import com.jetbrains.php.lang.psi.elements.Variable;
import com.jetbrains.php.lang.psi.resolve.types.PhpTypeProvider2;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;


/**
 * Class implements a type provider that enables auto-completion for
 * <p/>
 * $objectManager->get('ClassName')
 * <p/>
 * For further information on PSI etc. see http://confluence.jetbrains.com/display/PhpStorm/PHP+Open+API
 *
 * @author Sebastian Kurfürst
 */
public class ObjectManagerTypeProvider implements PhpTypeProvider2 {

	final static char TRIM_KEY = '\u0195';

	/**
	 * This is the "key" for the object manager autocompletion.
	 *
	 * @return
	 */
	@Override
	public char getKey() {
		return '\u0193';
	}

	/**
	 * FIRST STEP: if this method returns a STRING, getBySignature is CALLED.
	 *
	 * @param e
	 * @return
	 */
	@Nullable
	@Override
	public String getType(PsiElement e) {
		if (e instanceof MethodReference && !((MethodReference) e).isStatic()) {
			MethodReference methodReference = (MethodReference) e;

			// 1. Make sure the method is called "get".
			if (methodReference.getName().equals("get") && methodReference.getFirstChild() instanceof Variable) {

				Variable objectVariable = (Variable) methodReference.getFirstChild();

				// 2. Check whether type of variable is an object manager
				if (objectVariable.getType().getTypes().contains("\\TYPO3\\Flow\\Object\\ObjectManagerInterface")) {

					// 3. fetch the first parameter and make sure it is a string
					PsiElement[] parameters = methodReference.getParameters();
					if (parameters.length > 0) {
						PsiElement parameter = parameters[0];
						if (parameter instanceof StringLiteralExpression) {
							String param = ((StringLiteralExpression) parameter).getContents();
							if (StringUtil.isNotEmpty(param)) {
								// 4. return the method signature + parameter.
								// I am not sure why methodReference.getSignature() is exactly needed, but that seems to be the case.
								return methodReference.getSignature() + TRIM_KEY + param;
							}
						}
					}
				}
			}
		}

		return null;
	}

	/**
	 * SECOND STEP: if getType returns NON-NULL, getBySignature is called.
	 *
	 * @param expression
	 * @param project
	 * @return
	 */
	@Override
	public Collection<? extends PhpNamedElement> getBySignature(String expression, Project project) {
		String parameter = expression.substring(expression.lastIndexOf(TRIM_KEY) + 1);

		return PhpIndex.getInstance(project).getAnyByFQN(parameter);
	}
}