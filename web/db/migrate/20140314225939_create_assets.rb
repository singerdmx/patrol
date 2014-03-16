class CreateAssets < ActiveRecord::Migration
  def change
    create_table :assets do |t|
      t.string :number
      t.string :parent
      t.string :serialnum
      t.string :tag
      t.string :location
      t.text :description
      t.string :vendor
      t.string :failure_code
      t.string :manufacture
      t.integer :purchase_pri
      t.float :replace_cost
      t.datetime :install_date
      t.datetime :warranty_expire
      t.float :total_cost
      t.float :ytd_cost
      t.float :budget_cost
      t.integer :calnum

      t.timestamps
    end
    add_index :assets, :number

  end
end
