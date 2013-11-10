package de.sandstormmedia.flowstorm;

import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.util.ProcessingContext;
import com.jetbrains.php.PhpIndex;
import com.jetbrains.php.PhpIndexImpl;
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression;
import com.jetbrains.php.lang.psi.resolve.types.PhpType;
import org.jetbrains.annotations.NotNull;


/**
 * String class name completion contributor
 *
 * WORK IN PROGRESS!!!
 */
public class StringClassNameCompletionContributor extends CompletionContributor {

	@Override
	public void fillCompletionVariants(CompletionParameters parameters, CompletionResultSet result) {
		Logger.getInstance("x").warn("ct" + parameters.getCompletionType());
		if (parameters.getCompletionType() == CompletionType.BASIC && parameters.getPosition().getParent() instanceof StringLiteralExpression) {
			Project project = parameters.getPosition().getProject();
			Object x = PhpIndex.getInstance(project);

			Logger.getInstance("x").warn("foo" + x.getClass().getName());
			for (Object a : PhpIndex.getInstance(project).getAnyByFQN(null)) {
				Logger.getInstance("x").warn("foobar");
			}
			for (String className : PhpIndex.getInstance(project).getAllClassNames(PrefixMatcher.ALWAYS_TRUE)) {
				Logger.getInstance("x").warn(className);
				result.addElement(LookupElementBuilder.create(className));
			}
		}
		super.fillCompletionVariants(parameters, result);
	}
}