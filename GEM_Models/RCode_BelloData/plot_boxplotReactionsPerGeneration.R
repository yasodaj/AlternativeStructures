
library(ggplot2)
library(dplyr)

generateSummaryStats <- function(genNo){

  readPath = paste0("D:/BellosData/GF_3/Plot_Reaction/Gen_",genNo)
  savePath = paste0("D:/BellosData/GF_3/Plot_Reaction/")
  
  setwd(readPath)
  
  reactionFiles = list.files(pattern = "Gen_\\d+_.*.txt$")
  
  #mergeData
  myMergedData = do.call("rbind", lapply(reactionFiles, FUN = function(file) {
    read.table(file, header=TRUE, sep=",", stringsAsFactors = F)
  }))
  
  myMergedData$reactionFluxVal = as.numeric(as.character(myMergedData$reactionFluxVal))
  
  ###################################################################################################
  
  #box plot for reactions 
  
  #png(paste0(savePath,"fluxBoxplot/Gen_",genNo,"_reactionFluxBoxplot.png"), width = 1000, height = 600)
  
  plotA <- ggplot(data = myMergedData) + 
    geom_boxplot(aes(x = Reaction, y = reactionFluxVal), fill = "steelblue", color = "steelblue")+
    # geom_boxplot(data=myMergedData[myMergedData$Reaction %in% core,],
    #              aes(x = Reaction, y = reactionFluxVal),color="red") +
    labs(title=paste0("Generation ",genNo, " reaction flux distribution"),
         x="Reaction", y = "Reaction Flux") +
    theme(axis.text.x = element_text(angle = 90, hjust = 1, size = 8))
  
  ggsave(paste0(savePath,"fluxBoxplot/Gen_",genNo,"_reactionFluxBoxplot.png"),
         plotA,width = 12, height = 8, units = "in", dpi = 300, device='png')
  
  ####################################################################################################
  
  
  #na count - how many times a reaction did not appear in a model
  naCount = myMergedData %>% group_by(Reaction) %>% summarise(countNA = sum(is.na(reactionFluxVal)))
  
  #png(paste0(savePath,"NACount/Gen_",genNo,"_reactionNACount.png"), width = 1000, height = 600)
  
  plotB <- ggplot(data=naCount, aes(x=Reaction, y=countNA, group=1)) +
    geom_line(colour = "steel blue")+
    geom_point(colour = "steel blue", size = 2) +
    labs(title=paste0("Generation ",genNo," Reaction NA count for all models"),x="Reaction", y = "Count") + 
    theme_minimal() + 
    theme(axis.text.x = element_text(angle = 90, hjust = 1, size = 8)) 
  
  ggsave(paste0(savePath,"NACount/Gen_",genNo,"_reactionNACount.png"),
         plotB,width = 12, height = 8, units = "in", dpi = 300, device='png')  
  
  
  #no of reactions that did not appear any of the models
  
  naCountSummaryDF = naCount[naCount$countNA == 450, ][1]
  
  if(nrow(naCountSummaryDF) != 0){
      naCountSummaryDF = cbind(GenNo = genNo, naCountSummaryDF)
      write.table(naCountSummaryDF, paste0(savePath,"NACount/Gen_",genNo,"_allNAReactions.txt"),
                  sep = ",", quote = F, row.names = F)
  }
  
  
  ####################################################################################################
  
  
  #remove all rows where reationFlux is NA
  filteredNA = myMergedData[complete.cases(myMergedData[ , 2]),]
  
  #sum up the reaction flux values to find out which reactions  are always zero 
  zeroCount = filteredNA %>% group_by(Reaction) %>% summarise(reactionFluxVal = sum(reactionFluxVal))
  
  #filter the only zero ones - they appear in the model but the flux is always zero or > 0.00001
  zeroCountDF = zeroCount[zeroCount$reactionFluxVal == 0.0, ][1]
  
  if(nrow(zeroCountDF) != 0){
      zeroCountDF = cbind(GenNo = genNo, zeroCountDF)
      
      write.table(zeroCountDF, paste0(savePath,"ZeroReactions/Gen_",genNo,"_allZeroReactions.txt"),
                  sep = ",", quote = F, row.names = F)
  }
  #####################################################################################################
  
  
  positiveFlux = filteredNA[filteredNA$reactionFluxVal > 1,]
  positiveFluxCount = positiveFlux %>% group_by(Reaction) %>% summarise(reactionFluxVal = length(reactionFluxVal))
  
  positiveFluxCountDF = positiveFluxCount[positiveFluxCount$reactionFluxVal == 450, ][1]
  
  if(nrow(positiveFluxCountDF) != 0){
    positiveFluxCountDF = cbind(GenNo = genNo, positiveFluxCountDF)
    
    write.table(positiveFluxCountDF, paste0(savePath,"AlwaysPositive/Gen_",genNo,"_allPositiveReactions.txt"),
                sep = ",", quote = F, row.names = F)
  }
  
  #selecting the reactions that always appear in all the models
  positiveFluxFiltered = positiveFlux[positiveFlux$Reaction %in% positiveFluxCountDF$Reaction, ]
  
  positiveFluxSummary = positiveFluxFiltered %>% group_by(Reaction) %>% summarise(maxReactionFlux = max(reactionFluxVal),
                                                                                  minReactionFlux = min(reactionFluxVal),
                                                                                  avgReactionFlux = mean(reactionFluxVal))
  
  if(nrow(positiveFluxSummary) != 0){
    
      positiveFluxSummary = cbind(GenNo = genNo, positiveFluxSummary)
      
      write.table(positiveFluxSummary, paste0(savePath,"AlwaysPositive/Gen_",genNo,"_allPositiveReactionsFluxSummary.txt"),
                  sep = ",", quote = F, row.names = F)
  }
  
  
}

start.time = Sys.time()

generationNums = c(0:249)

lapply(generationNums, function(x) generateSummaryStats(x))

end.time = Sys.time()

t.time = end.time - start.time