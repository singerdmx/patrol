class CreateCheckManagers < ActiveRecord::Migration
  def change
    create_join_table :check_paths, :check_points, table_name: :check_manager do |t|

      t.index :check_path_id
      t.index :check_point_id

    end
  end
end
