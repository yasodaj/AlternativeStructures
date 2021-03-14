#inactive reaction percentage 

setwd("D:/BellosData/GF_3/Models")


getGens <- function(v) {
  pat <- paste0("(", paste0(v, collapse="|"), ")")
  return(pat)
}

computeReactionActivity <- function(reactionFile){
  
  dataR = read.table(reactionFile, sep = ",", header = T, stringsAsFactors = F)
  
  if(is.na(dataR) == T){
    
    return(NA)
    
  }else{
    dataR = dataR[dataR$Reaction !='Biomass', ]
    
    dataR$Activity = as.numeric(as.character(dataR$Activity))
    
    return(round(nrow(filter(dataR, Activity > 0.000001))/nrow(dataR), digits = 5))
  }
  
  
}

getReactionActivity <- function(genNo){
  
  pat <- paste0("^Generation_Flux_", getGens(genNo), "_Model_\\d+\\.txt$")
  reactionFiles = gtools::mixedsort(list.files(pattern = pat)) #models in one generation
  
  rowCombination = split(v<-t(utils::combn(450, 1)), seq(nrow(v))); rm(v)
  
  reactionActivity = lapply(rowCombination, 
                   function(x) computeReactionActivity(reactionFiles[x[1]]))
  
  reactionActivityDF <- data.frame(Model = sapply(rowCombination, `[`, 1),
                          reactionActivity = do.call(rbind, reactionActivity))
  # 
  # write.table(reactionActivityDF, 
  #             paste0("D:/BellosData/GF_3/Reaction_Activity/Gen_",genNo,"_reactionActivity.txt"), 
  #             sep = ",", quote = F, row.names = F)
  # 
  # 
  # 
  # reactionActivityDF$Model = factor(reactionActivityDF$Model, 
  #                                   levels = gtools::mixedsort(reactionActivityDF$Model))
  # 
  # plotB<- ggplot(data=reactionActivityDF, aes(x=Model, y=reactionActivity, group=1)) +
  #   geom_line(colour = "steel blue")+
  #   geom_point(colour = "steel blue", size = 2) +
  #   labs(title=paste0("Generation ",genNo," Active Reactions (Within model)"),
  #        x="Model", y = "Fraction of active reactions") + 
  #   theme_minimal() + 
  #   theme(axis.text.x = element_text(angle = 90, hjust = 1, size = 8)) + 
  #   ylim(0,1)
  # 
  # ggsave(paste0("D:/BellosData/GF_3/Reaction_Activity/Gen_",genNo,"_reactionActivity.png"), plotB,
  #        width = 16, height = 8, dpi = 250)
  # 
  
  
  biomassData = read.table(paste0("D:/BellosData/GF_3/Plot_Biomass/Gen_",genNo,"_biomass.txt"),
                           sep = ",", header = T ,stringsAsFactors = F)
  
  reactionActivityDF = cbind(reactionActivityDF, 
                       biomass = as.numeric(as.character(biomassData$biomassValue)))
  
  # reshaped = reshape2::melt(reactionActivityDF, id = c("Model"))
  # 
  # reshaped$Model = factor(reshaped$Model, levels = gtools::mixedsort(reshaped$Model))
  # 
  # plotB<- ggplot(data=reshaped, aes(x=Model, y=value, group=variable)) +
  #   geom_line(aes(color=variable))+
  #   geom_point(aes(color=variable)) +
  #   labs(title=paste0("Generation ",genNo," Reaction Activity and Biomass"),
  #        x="Model", y = "Activity Fraction") + 
  #   theme_minimal() + 
  #   theme(axis.text.x = element_text(angle = 90, hjust = 1, size = 8)) + 
  #   ylim(0,2) +
  #   scale_color_brewer(palette="Dark2")
  # 
  # ggsave(paste0("D:/BellosData/GF_3/Reaction_Activity/Gen_",genNo,"_ReactionActivity_biomass.png"), plotB,
  #        width = 16, height = 8, dpi = 250)
  
  
  geneData = read.table(paste0("D:/BellosData/GC_3/Clustering_activePercentage/Gen_",genNo,"_activePercentage.txt"),
                           sep = ",", header = T ,stringsAsFactors = F)
  
  
  reactionActivityDF = cbind(reactionActivityDF, 
                             geneActivity = as.numeric(as.character(geneData$ActivePercent)))
  
  
  reshapedWithGene = reshape2::melt(reactionActivityDF, id = c("Model"))
  
  reshapedWithGene$Model = factor(reshapedWithGene$Model, levels = gtools::mixedsort(reshapedWithGene$Model))
  
  plotB<- ggplot(data=reshapedWithGene, aes(x=Model, y=value, group=variable)) +
    geom_line(aes(color=variable))+
    geom_point(aes(color=variable)) +
    labs(title=paste0("Generation ",genNo," Reaction Activity, Gene Activity and Biomass"),
         x="Model", y = "Activity asa a fraction") + 
    theme_minimal() + 
    theme(axis.text.x = element_text(angle = 90, hjust = 1, size = 8)) + 
    ylim(0,2) +
    scale_color_brewer(palette="Dark2")
  
  ggsave(paste0("D:/BellosData/GF_3/Reaction_Activity/WithBiomassAndGeneActivity/Gen_",genNo,"_Reaction_GeneActivity_biomass.png"), plotB,
         width = 16, height = 8, dpi = 250)
  
}

sTime = Sys.time()

gens <- c(0:249)

lapply(gens, function(x) getReactionActivity(x))

eTime = Sys.time()
tTime = eTime - sTime


