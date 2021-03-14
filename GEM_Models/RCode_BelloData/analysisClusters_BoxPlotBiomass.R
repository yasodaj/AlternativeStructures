setwd("D:/BellosData/GF_3/Biomass_Analysis/Gen_36")


biomassData = read.table("D:/BellosData/GF_3/Plot_Biomass/Gen_36_biomass.txt", header = T, 
                         sep = ",", stringsAsFactors = F)

biomassData$biomass_metric <- as.numeric(as.character(biomassData$biomass_metric))

biomassData$biomass_metric <- round(biomassData$biomass_metric, digits = 4)


clusterFiles = list.files(pattern = "^Gen_\\d+_Biomass_hClustMemebers_K_\\d+.*.txt$")

biomassData['ClusterID'] <- 0

for(i in 1:length(clusterFiles)){
  
  clusterNo = as.numeric(unlist(str_extract_all(clusterFiles[i], "\\d+"))[3])
  
  models = read.table(clusterFiles[i], header = T, stringsAsFactors = F, sep = ",")
  
  modelIDs <- models[,1]
  
  biomassData$ClusterID[biomassData$M %in% modelIDs] <- clusterNo
  
}


ggplot(biomassData, aes(x=ClusterID, y=biomassValue)) + 
  geom_boxplot() 
  

