You have to manually add the generator element to each hbm.xml file to tell it
to use the postgres sequence generator:

e.g. add:

      <generator class="sequence">
        <param name="sequence">ct_users_sequence</param>
      </generator>

so it looks like:

<hibernate-mapping>
  <class name="au.com.nicta.ct.db.hibernate.CtUsers" schema="public" table="ct_users">
    <id name="pkUser" type="int">
      <column name="pk_user"/>
      <generator class="sequence">
        <param name="sequence">ct_users_sequence</param>
      </generator>
    </id>
    ...