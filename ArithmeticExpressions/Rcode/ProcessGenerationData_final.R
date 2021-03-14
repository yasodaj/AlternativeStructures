library(stringr)
options(scipen=999, digits = 2)
masterSummary <- data.frame()
uniqueSolutions <- data.frame()
uniqueSolutionsCount <- data.frame()
splitIterationTotal <- data.frame()

countOperator <- function(pat, expr){
  
  #pat = "\\+"
  #expr = "(2.0 + 3.0 + 5.0 + 7.0)"
  
  if(unlist(gregexpr(pattern = pat ,expr))[1] == (-1)){
    return(0)
  }else{
    return(length(unlist(gregexpr(pattern = pat ,expr))))
  }
  
}


for(x in 1:20){
  
  fname = paste0("D:\\GEP output ",x,"\\Target_60.0_pop_100_Iteration_1000_opr_mult.txt")
  
  rawData = read.table(fname,header = F, sep = "\n")
  solutions <- data.frame()
  
  
  # ----------------- extract generation data --------------- #  
  i = 1
  totalRecords <- nrow(rawData)
  gen = 0
  
  while(i < totalRecords){
    
    
    startingPoint = i + 1
    endingpoint = startingPoint + 99
    temp <- as.data.frame(rawData[c(startingPoint:endingpoint),])
    
    uniqueSolutions <- rbind(uniqueSolutions, data.frame(Run = x,
                                                              GenNo = gen,
                                                              UniqueSols = unique(temp)))
    
    uniqueSolutionsCount <- rbind(uniqueSolutionsCount, data.frame(Run = x,
                                                         GenNo = gen,
                                                         UniqueSolsCount = nrow(unique(temp))))
    
    temp <- NULL
    gen = gen + 1
    
    solutions <- rbind(solutions, data.frame(rawData[i,]))
    
    i = i + 101
    
  }
  
  colnames(solutions) <- "Iteration"
  
  
  splitIteration <- read.table(text = as.character(solutions$Iteration), sep = ",", stringsAsFactors = F)
  
  colnames(splitIteration) <- c("Iteration", "GenNum", "Pop size", "Target diff",
                                 "BestFitness_CurrentGen", "solution")
  
  splitIteration$ActualDiff <- (60 - splitIteration$BestFitness_CurrentGen)
  
  #splitIteration[!duplicated(splitIteration[,c('BestFitness_CurrentGen','solution')]),]
  
  lastRow <- nrow(splitIteration)
  
  masterSummary <- rbind(masterSummary, data.frame(splitIteration[lastRow,1], 
                                                   splitIteration[lastRow,2],
                                                   splitIteration[lastRow,6]))

  splitIterationTotal <- rbind(splitIterationTotal, splitIteration)
  
  rawData <- NULL
  solutions <- NULL
  splitIteration<-NULL
  
}

names(uniqueSolutions)[names(uniqueSolutions)=="rawData.c.startingPoint.endingpoint...."] <- "UniqueSolution"


colnames(masterSummary) <- c("RunNo", "GenNowithoptimalfitness", "OptimalSolution")
masterSummary$NumOperators <- nchar(as.character(masterSummary$OptimalSolution)) - nchar(gsub("\\+|\\-|\\*|\\/", "", as.character(masterSummary$OptimalSolution)))

as.numeric(countOperator("\\*", "(((26.0 + 20.0) + 22.0)"))

masterSummary$PlusOperator <- sapply(masterSummary$OptimalSolution, countOperator, pat = "\\+")
masterSummary$MultOperator <- sapply(masterSummary$OptimalSolution, countOperator, pat = "\\*")
masterSummary$SubOperator <- sapply(masterSummary$OptimalSolution, countOperator, pat = "\\-")
masterSummary$DivOperator <- sapply(masterSummary$OptimalSolution, countOperator, pat = "\\/")


lapply(masterSummary, FUN=countOperator, pat="\\+")

uniqueSolutions$NumOperators <- nchar(as.character(uniqueSolutions$UniqueSolution)) - nchar(gsub("\\+|\\-|\\*|\\/", "", as.character(uniqueSolutions$UniqueSolution)))
uniqueSolutions$expressionLength <- uniqueSolutions$NumOperators*2 + 1 

uniqueSolutions$PlusOperator <- sapply(uniqueSolutions$UniqueSolution, countOperator, pat = "\\+")
uniqueSolutions$MultOperator <- sapply(uniqueSolutions$UniqueSolution, countOperator, pat = "\\*")
uniqueSolutions$SubOperator <- sapply(uniqueSolutions$UniqueSolution, countOperator, pat = "\\-")
uniqueSolutions$DivOperator <- sapply(uniqueSolutions$UniqueSolution, countOperator, pat = "\\/")

uniqueSolutions$Value <- as.numeric(sapply(uniqueSolutions$UniqueSolution, function(x) lazyeval::lazy_eval(as.character(x))))
                                                        
write.table(masterSummary, "D:\\MasterSummary.txt", sep = "\t", quote = F, row.names = F)
write.table(uniqueSolutions, "D:\\UniqueSolutions.txt", sep = "\t", quote = F, row.names = F)
write.table(uniqueSolutionsCount, "D:\\UniqueSOlutionsCOunt.txt", sep = "\t", quote = F, row.names = F)





