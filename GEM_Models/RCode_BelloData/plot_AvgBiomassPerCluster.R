#plot common core in a heat map

setwd("D:/BellosData/GF_3/Biomass_Analysis/Gen_140")

processPath = "D:/BellosData/GF_3/Biomass_Analysis/Gen_140/CommonCoreReactions/"

generationNo = 140

biomassData = read.table(paste0("D:/BellosData/GF_3/Plot_Biomass/Gen_",generationNo,"_biomass.txt"),
                         header = T, sep = ",", stringsAsFactors = F)

clusterMembers = gtools::mixedsort(list.files(pattern = "^Gen_\\d+_Biomass_hClustMemebers_K_\\d+_memeber_\\d+.txt$"))

biomassData["ClusterID"] <- 0


for(i in 1:length(clusterMembers)){
  
  clusterNo = unlist(str_extract_all(clusterMembers[i],"\\d+"))[3]
  members = read.table(clusterMembers[i],header = T, stringsAsFactors = F)
  biomassData$ClusterID[biomassData$M %in% members[,1]] <- clusterNo
  
}

png(paste0(processPath,"Gen_",generationNo,"_clusterBiomassBoxplot.png"), width = 1200, height = 600)

ggplot(biomassData, aes(x=ClusterID, y=biomassValue)) + 
  geom_boxplot() +
  ylab("Biomass") +
  xlab("Cluster Id") +
  ylim(0,2) + 
  ggtitle(paste0("Generation ", generationNo, " - Biomass Boxplot "))

dev.off()



# 
# ggplot(biomassData, aes(x=ClusterID, y=biomassValue)) + 
#   stat_summary(fun.y="mean", geom="point", colour = "red") + 
#   stat_summary(fun.y="max", geom="point", colour = "blue") +
#   stat_summary(fun.y="min", geom="point", colour = "black") +
#   ylab("Biomass") +
#   xlab("Cluster Id") +
#   ylim(0,2) + 
#   ggtitle(paste0("Generation ", generationNo, " - Biomass Boxplot "))
