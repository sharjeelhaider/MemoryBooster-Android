package com.booster.avivast.antivirus;

/**
 * Created by hexdump on 03/02/16.
 */
public class ProblemFactory implements IFactory<IProblem>
{
    public IProblem createInstance(String constructionString) throws IllegalArgumentException
    {
        IProblem problem=null;
        switch(constructionString)
        {
            case DebugUSBEnabledProblem.kSerializationType:
                problem= new DebugUSBEnabledProblem();
                break;
            case AppProblem.kSerializationType:
                problem= new AppProblem();
                break;
            case UnknownAppEnabledProblem.kSerializationType:
                problem= new UnknownAppEnabledProblem();
                break;
            default:
                throw new IllegalArgumentException("Unknown node type creating IProblem");
        }

        return problem;
    }


}
