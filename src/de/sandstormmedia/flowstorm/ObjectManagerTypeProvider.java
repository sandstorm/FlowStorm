package de.sandstormmedia.flowstorm;

		import com.intellij.openapi.project.DumbService;
		import com.intellij.openapi.util.text.StringUtil;
		import com.intellij.psi.PsiElement;
		import com.jetbrains.php.lang.psi.elements.MethodReference;
		import com.jetbrains.php.lang.psi.elements.ParameterList;
		import com.jetbrains.php.lang.psi.elements.StringLiteralExpression;
		import com.jetbrains.php.lang.psi.elements.Variable;
		import com.jetbrains.php.lang.psi.resolve.types.PhpType;
		import com.jetbrains.php.lang.psi.resolve.types.PhpTypeProvider;
		import com.punktde.typo3storm.helpers.IdeHelper;
		import org.jetbrains.annotations.Nullable;


/**
 * Class implements a type provider that enables auto-completion for
 *
 * $objectManager->get('ClassName')
 *
 * For further information on PSI etc. see http://confluence.jetbrains.com/display/PhpStorm/PHP+Open+API
 *
 * @author Sebastian Kurfürst
 * @author Michael Knoll (mimi@kaktusteam.de)
 */
public class ObjectManagerTypeProvider implements PhpTypeProvider {

	@Nullable
	@Override
	public PhpType getType(PsiElement e) {
		if (DumbService.getInstance(e.getProject()).isDumb()) {
			return null;
		}

		if( ! IdeHelper.isPhpWithAutocompleteFeature()){
			return null;
		}

		// Make sure, given element is a non-static method reference
		if (e instanceof MethodReference && !((MethodReference)e).isStatic()) {
			MethodReference methodReference = (MethodReference)e;
			// Make sure, object on which method reference is called is a object manager
			if (methodReference.getName().equals("get")) {
				if (methodReference.getFirstChild() instanceof Variable) {
					Variable objectVariable = (Variable) methodReference.getFirstChild();
					// Check whether type of variable is an object manager
					if (objectVariable.getType().getTypes().contains("TYPO3\\Flow\\Object\\ObjectManagerInterface")) {
						PsiElement[] parameters = methodReference.getParameters();
						// Make sure, parameters are not empty and first parameter is a string
						if (parameters.length > 0 && parameters[0] instanceof StringLiteralExpression) {
							String param = ((StringLiteralExpression)parameters[0]).getContents();
							if (StringUtil.isNotEmpty(param)) {
								// Return new type from first parameter
								return new PhpType().add(param);
							}
						}
					}
				}
			}

		}

		return null;
	}

}