<select class="form-select ${(countryError??)?string('is-invalid', '')}" id="select-country">
    <option id="select-header" selected disabled hidden>Choose your country</option>
    <#if countries??>
        <#list Session.countries as country>
            <#if country == Session.selectedCountry!>
                <option id="selectedCountry" value="${country.id}" selected>${country}</option>
            <#else>
                <option id="country" value="${country.id}">${country}</option>
            </#if>
        </#list>
    </#if>
</select>
