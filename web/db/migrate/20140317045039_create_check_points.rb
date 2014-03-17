class CreateCheckPoints < ActiveRecord::Migration
  def change
    create_table :check_points do |t|
      t.integer :cstm_tpmid
      t.text :description
      t.integer :hasld
      t.string :period_unit
      t.string :standard
      t.string :status
      t.string :hi_warn
      t.integer :period
      t.string :warn_type
      t.string :site_id
      t.string :tpm_num
      t.string :operator
      t.string :lo_warn
      t.string :tpm_type
      t.string :lo_danger
      t.integer :looknum
      t.references :asset

      t.timestamps
    end
    add_index :check_points, :cstm_tpmid
  end
end
