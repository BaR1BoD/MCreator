<#if data.hasGUITexture?? && data.hasGUITexture()>
{
  "model": {
    "type": "minecraft:select",
    "property": "minecraft:display_context",
    "cases": [
      {
        "when": ["gui", "fixed", "ground"],
        "model": {
          "type": "minecraft:model",
          "model": "${modid}:item/${registryname}_gui"
        }
      }
    ],
    "fallback": {
      <@defaultItemModel/>
    }
  }
}
<#else>
{
  "model": {
    <@defaultItemModel/>
  }
}
</#if>

<#macro defaultItemModel>
  <#assign models = (data.getModels??)?then(data.getModels(), [])>
  <#if models?has_content>
    "type": "${modid}:legacy_overrides",
    "overrides": [
      <#list models as model>
      {
        "predicate": [
          <#list model.stateMap.entrySet() as entry>
          {
            "property": {
            <#if entry.getKey().getName() == "damage">
              "property": "minecraft:damage",
              "normalize": true
            <#elseif entry.getKey().getName() == "lefthanded">
              "property": "${modid}:lefthanded"
            <#else>
              "property": "${generator.map(entry.getKey().getPrefixedName(registryname + "/"), "itemproperties")}"
            </#if>
            },
            "value": ${entry.getValue()}
          }<#sep>,
          </#list>
        ],
        "model": {
          "type": "minecraft:model",
          "model": "${modid}:item/${registryname}_${model?index}"
        }
      }<#sep>,
      </#list>
    ],
    "fallback": {
      "type": "minecraft:model",
      "model": "${modid}:item/${registryname}"
    }
  <#else>
    "type": "minecraft:model",
    <#if var_sufix??>
    "model": "${modid}:item/${registryname}${var_sufix}"
    <#else>
    "model": "${modid}:item/${registryname}"
    </#if>
  </#if>
</#macro>